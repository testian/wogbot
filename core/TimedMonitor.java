/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

/**
 *
 * @author testi
 */
public class TimedMonitor {
private long time;

public TimedMonitor() {
time = 0;
}
synchronized public void setMonitoringDuration(long duration) {
time = duration + System.currentTimeMillis();
}

synchronized public void setMonitoringTime(long time) {
this.time = time;
}

synchronized public void increaseDuration(long duration) {
if (time < System.currentTimeMillis()) {
setMonitoringDuration(duration);
}
else {
time+=duration;
}
}

public boolean doMonitor() {
return (time>=System.currentTimeMillis());
}

}
