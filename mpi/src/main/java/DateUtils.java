import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class DateUtils {
    public static TreeMap<String, List<Date>> genDates(Date startDate, Date endDate, List<Date> availableDates, int mode) {
        TreeMap<String, List<Date>> dates = new TreeMap<String, List<Date>>();
        Date currentDate = startDate;
        if (mode == 1) {
            // month data
            while (currentDate.before(endDate)) {
                List<Date> d = new ArrayList<Date>();
                d.add(currentDate);
                dates.put(Utils.getMonthString(currentDate), d);
                currentDate = Utils.addMonth(currentDate);
            }
        } else if (mode == 2) {
            while (currentDate.before(endDate)) {
                String startName = Utils.getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = Utils.addMonth(tempDate);
                }
                currentDate = tempDate;
                String endDateName = Utils.getMonthString(tempDate);
                dates.put(startName + "_" + endDateName, d);
            }
        } else if (mode == 3) {
            List<Date> d = new ArrayList<Date>();
            while (currentDate.before(endDate)) {
                d.add(currentDate);
                currentDate = Utils.addMonth(currentDate);
            }
            dates.put(Utils.getMonthString(startDate) + "_" + Utils.getMonthString(endDate), d);
        } else if (mode == 4) {
            while (currentDate.before(endDate)) {
                String startName = Utils.getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = Utils.addMonth(tempDate);
                }
                currentDate = Utils.addMonth(currentDate);
                String endDateName = Utils.getMonthString(tempDate);
                dates.put(startName + "_" + endDateName, d);
                if (!tempDate.before(endDate)) {
                    break;
                }
            }
        } else if (mode == 5) {
            Date lastDate;
            do {
                lastDate = Utils.addYear(currentDate);
                String start = Utils.getDateString(currentDate);
                String end = Utils.getDateString(lastDate);
                List<Date> list = new ArrayList<Date>();
                list.add(currentDate);
                list.add(lastDate);

                currentDate = Utils.addDays(currentDate, 7);
                dates.put(start + "_" + end, list);
            } while (lastDate.before(endDate));
        } else if (mode == 6) {
            // first lets get the date 1 year away
            Date oneYearAway = Utils.addYear(startDate);
            int firstIndex = 0;
            for (int i = 0; i < availableDates.size(); i++) {
                Date d = availableDates.get(i);
                if (d.equals(startDate) || d.after(startDate)) {
                    firstIndex = i;
                    break;
                }
            }

            int lastIndex = 0;
            for (int i = 0; i < availableDates.size(); i++) {
                Date d = availableDates.get(i);
                if (d.equals(oneYearAway) || d.after(oneYearAway)) {
                    lastIndex = i;
                    break;
                }
            }
            if (lastIndex != 0) {
                for (; lastIndex < availableDates.size(); lastIndex++) {
                    Date lastDate = availableDates.get(lastIndex);
                    addTwoDates(availableDates.get(firstIndex++), lastDate, dates);
                    if (lastDate.after(endDate) || lastDate.equals(endDate)) {
                        break;
                    }
                }
            }
        } else if (mode == 7) {
            Date lastDate = Utils.addYear(currentDate);;
            do {
                String start = Utils.getDateString(currentDate);
                String end = Utils.getDateString(lastDate);
                List<Date> list = new ArrayList<Date>();
                list.add(currentDate);
                list.add(lastDate);

                lastDate = Utils.addDays(lastDate, 7);
                dates.put(start + "_" + end, list);
            } while (lastDate.before(endDate));
        }
        return dates;
    }

    public static List<String> genDateList(Date startDate, Date endDate, int mode, List<Date> availableDates) {
        TreeMap<String, List<Date>> dates = new TreeMap<String, List<Date>>();
        List<String> dateList = new ArrayList<String>();
        Date currentDate = startDate;
        if (mode == 1) {
            // month data
            while (currentDate.before(endDate)) {
                List<Date> d = new ArrayList<Date>();
                d.add(currentDate);
                dates.put(Utils.getMonthString(currentDate), d);
                currentDate = Utils.addMonth(currentDate);
            }
        } else if (mode == 2) {
            while (currentDate.before(endDate)) {
                String startName = Utils.getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = Utils.addMonth(tempDate);
                }
                currentDate = tempDate;
                String endDateName = Utils.getMonthString(tempDate);
                dates.put(startName + "_" + endDateName, d);
            }
        } else if (mode == 3) {
            List<Date> d = new ArrayList<Date>();
            while (currentDate.before(endDate)) {
                d.add(currentDate);
                currentDate = Utils.addMonth(currentDate);
            }
            dates.put(Utils.getMonthString(startDate) + "_" + Utils.getMonthString(endDate), d);
        } else if (mode == 4) {
            while (currentDate.before(endDate)) {
                String startName = Utils.getMonthString(currentDate);
                Date tempDate = currentDate;
                List<Date> d = new ArrayList<Date>();
                for (int i = 0; i < 12; i++) {
                    d.add(tempDate);
                    tempDate = Utils.addMonth(tempDate);
                }
                currentDate = Utils.addMonth(currentDate);
                String endDateName = Utils.getMonthString(tempDate);
                String key = startName + "_" + endDateName;
                dates.put(key, d);
                dateList.add(key);
                if (!tempDate.before(endDate)) {
                    break;
                }
            }
        } else if (mode == 5) {
            Date lastDate;
            do {
                lastDate = Utils.addYear(currentDate);
                String start = Utils.getDateString(currentDate);
                String end = Utils.getDateString(lastDate);
                List<Date> list = new ArrayList<Date>();
                list.add(currentDate);
                list.add(lastDate);

                currentDate = Utils.addDays(currentDate, 7);
                String key = start + "_" + end;
                dates.put(key, list);
                dateList.add(key);
            } while (lastDate.before(endDate));
        } else if (mode == 6) {
            // first lets get the date 1 year away
            Date oneYearAway = Utils.addYear(startDate);
            int firstIndex = 0;
            for (int i = 0; i < availableDates.size(); i++) {
                Date d = availableDates.get(i);
                if (d.equals(startDate) || d.after(startDate)) {
                    firstIndex = i;
                    break;
                }
            }

            int lastIndex = 0;
            for (int i = 0; i < availableDates.size(); i++) {
                Date d = availableDates.get(i);
                if (d.equals(oneYearAway) || d.after(oneYearAway)) {
                    lastIndex = i;
                    break;
                }
            }
            if (lastIndex != 0) {
                for (; lastIndex < availableDates.size(); lastIndex++) {
                    Date lastDate = availableDates.get(lastIndex);
                    addTwoDatesKey(availableDates.get(firstIndex++), lastDate, dateList);
                    if (lastDate.after(endDate) || lastDate.equals(endDate)) {
                        break;
                    }
                }
            }
        } else if (mode == 7) {
            Date lastDate = Utils.addYear(currentDate);;
            do {
                String start = Utils.getDateString(currentDate);
                String end = Utils.getDateString(lastDate);
                List<Date> list = new ArrayList<Date>();
                list.add(currentDate);
                list.add(lastDate);

                lastDate = Utils.addDays(lastDate, 7);
                String key = start + "_" + end;
                dates.put(key, list);
                dateList.add(key);
            } while (lastDate.before(endDate));
        }
        return dateList;
    }

    private static void addTwoDatesKey(Date currentDate, Date lastDate, List<String> dateList) {
        String start = Utils.getDateString(currentDate);
        String end = Utils.getDateString(lastDate);
        dateList.add(start + "_" + end);
    }

    private static void addTwoDates(Date currentDate, Date lastDate, TreeMap<String, List<Date>> dates) {
        String start = Utils.getDateString(currentDate);
        String end = Utils.getDateString(lastDate);
        List<Date> list = new ArrayList<Date>();
        list.add(currentDate);
        list.add(lastDate);
        dates.put(start + "_" + end, list);
    }

    public static Set<Date> retrieveDates(String fileName) throws FileNotFoundException {
        FileReader input = new FileReader(fileName);
        BufferedReader bufRead = new BufferedReader(input);
        Record record;
        CleanMetric cleanMetric = new CleanMetric();
        int count = 0;
        Set<Date> dates = new HashSet<Date>();
        while ((record = Utils.parseFile(bufRead, cleanMetric, true)) != null) {
            dates.add(record.getDate());
        }
        return dates;
    }

    public static List<Date> sortDates(Set<Date> allDates) {
        ArrayList<Date> list = new ArrayList<Date>(allDates);
        Collections.sort(list);
        return list;
    }
}
