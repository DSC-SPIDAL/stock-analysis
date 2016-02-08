package edu.indiana.soic.ts.utils;

import com.google.protobuf.ServiceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TableUtils {
    private static final Logger log = LoggerFactory.getLogger(TableUtils.class);

    public static List<String> getDates(String start, String end) throws ParseException {
        List<String> allDates = new ArrayList<String>();
        Date startDate = getDate(start);
        Date endDate = getDate(end);
        ResultScanner scannerForDateTable = getScannerForDateTable();
        for (Result aResultScanner : scannerForDateTable) {
            String date = new String(aResultScanner.getRow());
            Date rowDate = getDate(date);
            if (startDate.compareTo(rowDate) * rowDate.compareTo(endDate) > 0){
                allDates.add(date);
            }
        }
        return allDates;

    }

    public static Date getDate (String date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.parse(date);
    }

    public static String convertDateToString (Date date) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        return df.format(date);
    }

    private static ResultScanner getScannerForDateTable() {
        try {
            Configuration configuration = HBaseConfiguration.create();
            HBaseConfiguration.addHbaseResources(configuration);
            HBaseAdmin.checkHBaseAvailable(configuration);
            Connection connection = ConnectionFactory.createConnection(configuration);
            // Instantiating HbaseAdmin class
            Admin admin = connection.getAdmin();

            HTableDescriptor[] tableDescriptor = admin.listTables();
            // printing all the table names.
            for (HTableDescriptor aTableDescriptor : tableDescriptor) {
                if (aTableDescriptor.getTableName().getNameAsString().equals(Constants.STOCK_DATES_TABLE)) {
                    Table table = connection.getTable(aTableDescriptor.getTableName());
                    Scan scan = new Scan();
                    scan.setCaching(20);
                    scan.addFamily(Constants.STOCK_DATES_CF_BYTES);
                    return table.getScanner(scan);
                }
            }
        } catch (ServiceException e) {
            log.error("Error while reading Stock Dates table", e);
        } catch (MasterNotRunningException e) {
            log.error("Error while reading Stock Dates table", e);
        } catch (ZooKeeperConnectionException e) {
            log.error("Error while reading Stock Dates table", e);
        } catch (IOException e) {
            log.error("Error while reading Stock Dates table", e);
        }
        return null;
    }

    public static String getMonthString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.MONTH) + 1);
    }

    public static String getDateString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String month = String.format("%02d", (cal.get(Calendar.MONTH) + 1));
        String day = String.format("%02d", (cal.get(Calendar.DATE)));
        return cal.get(Calendar.YEAR) + month + day;
    }

    public static Date addYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }

    public static Date addYear(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Date addMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    public static Date addDays(Date data, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static TreeMap<String, List<Date>> genDates(Date startDate, Date endDate, int interval, TimeUnit intervalUnit, int frontShit, int tailShift, TimeUnit shiftUnit) {
        TreeMap<String, List<Date>> dates = new TreeMap<String, List<Date>>();
        Date currentStartDate = startDate;
        Date currentEndDate;
        currentEndDate = shiftDates(currentStartDate, interval, intervalUnit);
        // now we shift the front and back dates
        do {
            String start = getDateString(currentStartDate);
            String end = getDateString(currentEndDate);
            List<Date> list = new ArrayList<Date>();
            list.add(currentStartDate);
            list.add(currentEndDate);
            dates.put(start + "_" + end, list);

            currentStartDate = shiftDates(currentStartDate, tailShift, shiftUnit);
            currentEndDate = shiftDates(currentEndDate, frontShit, shiftUnit);
        } while (currentEndDate.before(endDate));

        return dates;
    }

    public static Date shiftDates(Date date, int shift, TimeUnit shiftUnit) {
        Date shitfDate;
        if (shiftUnit == TimeUnit.DAYS) {
            shitfDate = addDays(date, shift);
        } else {
            shitfDate = addYear(date, shift);
        }
        return shitfDate;
    }
}
