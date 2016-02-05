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

    public static Date addYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, 1);
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

    public static TreeMap<String, List<Date>> genDates(Date startDate, Date endDate, int mode) {
        TreeMap<String, List<Date>> dates = new TreeMap<String, List<Date>>();
        Date currentDate = startDate;
        if (mode == 1) {
            // month data
            while (currentDate.before(endDate)) {
                List<Date> d = new ArrayList<Date>();
                d.add(currentDate);
                dates.put(getMonthString(currentDate), d);
                currentDate = addMonth(currentDate);
            }
        } else if (mode == 2) {
            while (currentDate.before(endDate)) {
                String startName = getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = addMonth(tempDate);
                }
                currentDate = tempDate;
                String endDateName = getMonthString(tempDate);
                dates.put(startName + "_" + endDateName, d);
            }
        } else if (mode == 3) {
            List<Date> d = new ArrayList<Date>();
            while (currentDate.before(endDate)) {
                d.add(currentDate);
                currentDate = addMonth(currentDate);
            }
            dates.put(getMonthString(startDate) + "_" + getMonthString(endDate), d);
        } else if (mode == 4) {
            while (currentDate.before(endDate)) {
                String startName = getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = addMonth(tempDate);
                }
                currentDate = addMonth(currentDate);
                String endDateName = getMonthString(tempDate);
                dates.put(startName + "_" + endDateName, d);
                if (!tempDate.before(endDate)) {
                    break;
                }
            }
        } else if (mode == 5) {
            Date lastDate;
            do {
                lastDate = addYear(currentDate);
                String start = getDateString(currentDate);
                String end = getDateString(lastDate);
                List<Date> list = new ArrayList<Date>();
                list.add(currentDate);
                list.add(lastDate);

                currentDate = addDays(currentDate, 7);
                dates.put(start + "_" + end, list);
            } while (lastDate.before(endDate));
        } else if (mode == 6) {
            Date lastDate;
            do {
                lastDate = addYear(currentDate);
                String start = getDateString(currentDate);
                String end = getDateString(lastDate);
                List<Date> list = new ArrayList<Date>();
                list.add(currentDate);
                list.add(lastDate);

                currentDate = addDays(currentDate, 1);
                dates.put(start + "_" + end, list);
            } while (lastDate.before(endDate));
        } else if (mode == 7) {
            Date lastDate = addYear(currentDate);;
            do {
                String start = getDateString(currentDate);
                String end = getDateString(lastDate);
                List<Date> list = new ArrayList<Date>();
                list.add(currentDate);
                list.add(lastDate);

                lastDate = addDays(lastDate, 7);
                dates.put(start + "_" + end, list);
            } while (lastDate.before(endDate));
        }
        return dates;
    }
}
