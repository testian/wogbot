/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networkreporter;
//import jcifs.Config;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.net.InetAddress;
import jcifs.netbios.NbtAddress;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
/**
 *
 * @author Dimitri Nüscheler
 */
public class SmbNetReporter extends NetReporter {
    SmbFile root;
    //List<NbtAddress> hosts;
    Map<String,byte[]> hosts;
    Map<byte[],String> inverseHosts;

    private InetAddress network;
    private int subnetBits;
    private int threadCount;
    public SmbNetReporter(byte[] network, int subnetBits, int threadCount) {
        super();
        //jcifs.Config.setProperty("jcifs.ResolveOrder", "BCAST");
        try {
        root = new SmbFile("smb://");
        } catch (MalformedURLException ex) {
        root = null;
        }
        hosts = new HashMap<String,byte[]>();
        inverseHosts = new HashMap<byte[],String>();
        if (subnetBits < 8 || subnetBits > 32)
            throw new IllegalArgumentException("subnetBits must be in range of 8-32");
        this.threadCount = threadCount;
        this.subnetBits = subnetBits;
        if (network == null){this.network = null;return;}
        try {
        this.network = InetAddress.getByAddress(network);
        if (!(this.network instanceof java.net.Inet4Address)) throw new IllegalArgumentException("IPv4 address required");
        if (this.network.getAddress()[3]!=0){throw new IllegalArgumentException("network must be a network address");}
        } catch (UnknownHostException ex) {
        throw new IllegalArgumentException(ex.getMessage());
        }
    }
    public SmbNetReporter() {
    this(null,0,0);
    }

    public void scan() {
    try {
        Map<String,byte[]> newHosts = getHosts();
        matchHosts(newHosts);
    } catch (SmbException ex) {
    System.err.println(ex.getMessage());
    }
    }
    private Map<String,byte[]> getHosts() throws SmbException {
    HashMap<String,byte[]> newHosts = new HashMap<String,byte[]>();
    if (network == null) {
    scanForHosts(root, newHosts, 0);
    
    }
    else {
    scanForHostsWithoutBroadcast(newHosts);
    }
    return newHosts;
    }
    private void scanForHostsWithoutBroadcast(Map<String,byte[]> hList) throws SmbException {
        
        int networkSize = 1<<(32-subnetBits); //including networks and broadcasts
        


        if (threadCount>0) {




        class Scanner implements Runnable {
        int start, end;
        public Scanner(int start, int end) {
        this.start = start;
        this.end = end;
        }
        Map<String, byte[]> result;
            public void run() {
                result = scanRange(start, end);
            }

        }

        int perThreadScans = networkSize/threadCount;

        Scanner[] scanners = new Scanner[threadCount];
        for (int i = 0; i < threadCount; i++) {
            if (i<threadCount-1)
            scanners[i] = new Scanner(perThreadScans*i,perThreadScans*(i+1));
            else
            scanners[i] = new Scanner(perThreadScans*i,networkSize);
        }
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
        threads[i] = new Thread(scanners[i]);
        threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
        try {
        threads[i].join();
        hList.putAll(scanners[i].result);
        } catch (InterruptedException ex){System.err.println(ex);}
        }
        }
        else {
        hList.putAll(scanRange(0,networkSize));
        }

        

    
    }

    private Map<String, byte[]> scanRange(int start, int end) {
        byte[] netAddress = network.getAddress();
        Map<String,byte[]> hList = new HashMap<String, byte[]>();

        for (int i = start; i < end; i++) {

        byte[] address = addressAtIndex(netAddress,i);
        if (address[3] != 0 && address[3] != (byte)255)
        {
        try {
        //System.out.println("Checking " + InetAddress.getByAddress(address).getHostAddress());
        NbtAddress[] nbta = NbtAddress.getAllByAddress(InetAddress.getByAddress(address).getHostAddress());
        //System.out.println("Hosts found for " + InetAddress.getByAddress(address).getHostAddress());
        for (NbtAddress n : nbta) {
        //System.out.println(n.getHostName());
        }
        //System.out.println("End of list");
        hList.put(nbta[0].getHostName(), Arrays.copyOf(address, address.length));
        } catch (UnknownHostException ex) {
            //Host not found, go on.
        }
        }
        }
        return hList;
    }

    private byte[] addressAtIndex(byte[] addr, int index) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(ByteBuffer.wrap(addr).getInt()+index);
        return b.array();
    }
    private void scanForHosts(SmbFile node, Map<String,byte[]> hList, int level) throws SmbException {
    if (level >= 2) {
    String name = node.getName();
    if (name.endsWith("/")) {
    name = name.substring(0,name.length()-1);
    System.out.print("Found " + name);
    try {
    NbtAddress address = NbtAddress.getByName(name);
    hList.put(name, address.getAddress());
    System.out.println(" and resolved: " + address);
    } catch (UnknownHostException ex) {
        System.out.println(", but could not resolve");
        System.err.println(ex);
    }
    }
    }
    else if (node.isDirectory()) {
    for (SmbFile f : node.listFiles()) {
    scanForHosts(f,hList,level+1);
    }
    }

    }
    private void matchHosts(Map<String,byte[]> newHosts) {

        for (Map.Entry<String,byte[]> e : newHosts.entrySet()) {
        byte[] oldAddr = hosts.get(e.getKey());
        if (oldAddr == null) {
        String oldHostName = inverseHosts.get(e.getValue());
        if (oldHostName == null) {
        //neither ip nor host existed previously
        //New host detected
        this.notifyJoin(inetAddr(e.getKey(), e.getValue()));
        } else {
        this.notifyChange(inetAddr(oldHostName,e.getValue()), inetAddr(e.getKey(),e.getValue()));
        hosts.remove(oldHostName);
        }
        } else {
        if (!Arrays.equals(oldAddr,e.getValue())) {
        notifyChange(inetAddr(e.getKey(),oldAddr),inetAddr(e.getKey(),e.getValue()));
        
        }
        hosts.remove(e.getKey());
        }
        }
        for (Map.Entry<String, byte[]> e : hosts.entrySet()) {

            try {
            NbtAddress reResolve = NbtAddress.getByName(e.getKey());
            if (!Arrays.equals(reResolve.getAddress(),e.getValue())) {
            this.notifyChange(inetAddr(e.getKey(),e.getValue()), inetAddr(reResolve.getHostName(), reResolve.getAddress()));
            }
            newHosts.put(reResolve.getHostName(), reResolve.getAddress());
            }
            catch (UnknownHostException ex) {
            this.notifyLeave(inetAddr(e.getKey(),e.getValue()));
            }
        

        }
        hosts = newHosts;
        inverseHosts.clear();
        for (Map.Entry<String, byte[]> e : hosts.entrySet()) {
        inverseHosts.put(e.getValue(), e.getKey());
        }
    }
    private InetAddress inetAddr(String name, byte[] ip) {
    try {
        return InetAddress.getByAddress(name, ip);} catch (UnknownHostException ex){return null;}
    }

    @Override
    public List<InetAddress> onlineHosts() {
        List<InetAddress> list = new ArrayList<InetAddress>();
        for (Map.Entry<String,byte[]> e : hosts.entrySet()) {
        list.add(inetAddr(e.getKey(),e.getValue()));
        }
        return list;
    }




}
