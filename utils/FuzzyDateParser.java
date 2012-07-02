package utils;
import java.util.*;
import java.text.*;
public class FuzzyDateParser {
Locale locale;
long time;

public FuzzyDateParser(Locale locale, long time) {
	this.locale = locale;
	this.time = time;
}
public Date parse(String dateString) throws ParseException
{
	String[] dateCombo = {"dd.MM.yy", "d.M.yy"};
	String[] timeCombo = {"HH:mm", "HH:mm:ss"};
	
	for (int i = 0; i< dateCombo.length;i++) {
		for (int j = 0; j< timeCombo.length;j++) {
		
		try {
		return new SimpleDateFormat(dateCombo[i] + " " + timeCombo[j],locale).parse(dateString);
	}catch (ParseException ex){}
		}
	}
	
	SimpleStringTokenizer dateTokens = new SimpleStringTokenizer(dateString);
	
	//String weekday=null;
	String monthday=null;
	String month=null;
	String year=null;

	String hour=null;
	String minute=null;
	String second=null;
	
	while (dateTokens.hasMoreTokens())
	{
	String token = dateTokens.nextToken();
	//try {weekday = parseWeekday(token);}catch(ParseException ex){}
	try {monthday = parseMonthday(token);}catch(ParseException ex){}
	try {month = parseMonth(token);}catch(ParseException ex){}
	try {year = parseYear(token);}catch(ParseException ex){}
	try {hour = parseHour(token);}catch(ParseException ex){}
	try {minute = parseMinute(token);}catch(ParseException ex){}
	try {second = parseSecond(token);}catch(ParseException ex){}
	}
	
	
	String finalParsePattern="";
	//if (weekday == null) {weekday = new SimpleDateFormat("EEEEE",locale).format(new Date(time)) + " ";}
	
	if (monthday == null) {monthday = new SimpleDateFormat("d.",locale).format(new Date(time));}
	if (month == null) {month = new SimpleDateFormat("MMMMM",locale).format(new Date(time));}
	if (year == null) {year = new SimpleDateFormat("yyyy",locale).format(new Date(time));}
	if (hour == null) {hour = new SimpleDateFormat("HH ",locale).format(new Date(time));}
	if (minute == null) {minute = new SimpleDateFormat("mm",locale).format(new Date(time));}
	if (second == null) {second = new SimpleDateFormat("ss",locale).format(new Date(time));}
	
	finalParsePattern = "d. MMMMM yyyy HH mm ss";
	String finalParseString=monthday + " "+ month + " "+ year +" "+ hour + " "+ minute +"  " + second;
	
	//System.out.println("String: " + finalParseString);
	//System.out.println("Pattern: " + finalParsePattern);
	/*if (!new SimpleDateFormat("EEEEE",locale).format(new SimpleDateFormat("EEEEE",locale).parse(weekday)).equals(new SimpleDateFormat("EEEEE").format(new SimpleDateFormat(finalParsePattern,locale).parse(finalParseString))))
	{
		throw new ParseException("Widersprüchliche Datumsangabe",0);
	}*/
	
	

	return new SimpleDateFormat(finalParsePattern,locale).parse(finalParseString);
}

/*private String parseWeekday(String token) throws ParseException
{
String stripped = token.replaceAll("[^A-Za-z]","");

new SimpleDateFormat("EEEEE",locale).parse(stripped);

return stripped;
}*/
private String parseMonthday(String token) throws ParseException
{

		new SimpleDateFormat("d.",locale).parse(token);


		
		return token;
}
private String parseYear(String token) throws ParseException
{
	String stripped = token;//token.replaceAll("[^0-9:]","");
	if (stripped.replaceAll("[0-9]", "").length()>0){throw new ParseException("invalid content",0);}
	try {
		new SimpleDateFormat("yyyy",locale).parse(stripped);
		} catch (ParseException ex){}
			new SimpleDateFormat("yy",locale).parse(stripped);
			
	return stripped;			

}
	
private String parseMonth(String token) throws ParseException
{
	String stripped = token;//token.replaceAll("[^0-9]:","");
	if (stripped.replaceAll("[0-9]", "").length()>0){throw new ParseException("invalid content",0);}
	new SimpleDateFormat("MMMMM",locale).parse(stripped);
	return stripped;
}





private String parseHour(String token) throws ParseException
{
	SimpleStringTokenizer subTokens = new SimpleStringTokenizer(token,':');
	
	if (subTokens.hasMoreTokens())
	{
		String subtoken=subTokens.nextToken();
		new SimpleDateFormat("HH",locale).parse(subtoken);
		if (!subTokens.hasMoreTokens()){throw new ParseException("Expected minute after hour",0);}
		return subtoken;
	}
	throw new ParseException("Empty",0);
}
private String parseMinute(String token) throws ParseException
{
	SimpleStringTokenizer subTokens = new SimpleStringTokenizer(token,':');
	subTokens.nextToken();
	if (subTokens.hasMoreTokens())
	{
		String subtoken=subTokens.nextToken();
		new SimpleDateFormat("MM",locale).parse(subtoken);
		return subtoken;
	}
	throw new ParseException("Empty",0);
}



private String parseSecond(String token) throws ParseException
{
	SimpleStringTokenizer subTokens = new SimpleStringTokenizer(token,':');
	subTokens.nextToken();subTokens.nextToken();
	if (subTokens.hasMoreTokens())
	{
		String subtoken=subTokens.nextToken();
		new SimpleDateFormat("HH",locale).parse(subtoken);
		return subtoken;
	}
	throw new ParseException("Empty",0);
}





	
}
