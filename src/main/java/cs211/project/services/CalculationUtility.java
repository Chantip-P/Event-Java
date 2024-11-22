package cs211.project.services;

import cs211.project.models.Trackable;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class CalculationUtility {
    public static String formatDate(Calendar dateTime) {
        if (dateTime != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormat.format(dateTime.getTime());
        } else {
            return "MM/DD/YYYY";
        }
    }

    public static String formatTime(Calendar dateTime, boolean getSecond) {
        if (dateTime != null) {
            int hour = dateTime.get(Calendar.HOUR_OF_DAY);
            int minute = dateTime.get(Calendar.MINUTE);
            if (getSecond) {
                int second = dateTime.get(Calendar.SECOND);
                return String.format("%02d:%02d:%02d", hour, minute, second);
            }
            return String.format("%02d:%02d", hour, minute);
        } else {
            if (getSecond) { return "HH:MM:SS"; }
            return "HH:MM";
        }
    }

    public static String formatDateTime(Calendar dateTime, boolean getSecond) {
        return formatDate(dateTime) + " " + formatTime(dateTime, getSecond);
    }

    public static Calendar localDateToCalendar(LocalDate localDate, int hour, int minute) {
        if (localDate == null) { return null; }

        GregorianCalendar gc = GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        gc.set(GregorianCalendar.HOUR_OF_DAY, hour);
        gc.set(GregorianCalendar.MINUTE, minute);
        return gc;
    }

    public static LocalDate calendarToLocalDate(Calendar calendar) {
        return LocalDate.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
    }

    public static int generateIDFromDataList(Collection<? extends Trackable> trackables) {
        int maxID = -1;
        for (Trackable t : trackables) {
            maxID = Math.max(maxID, t.getID());
        }
        return maxID + 1;
    }

    public static File findFile(String fileDirectory, String fileName) {
        File directory = new File(fileDirectory);
        if (!directory.exists()) { directory.mkdirs(); }

        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                String fn = f.getName();
                String fnName = fn.substring(0, fn.lastIndexOf("."));
                if (fnName.equals(fileName) || fn.equals(fileName)) {
                    return f;
                }
            }
        }

        return null;
    }

    public static boolean renameFile(String fileDirectory, String oldName, String newName) {
        File file = findFile(fileDirectory, oldName);
        if (file == null || oldName.equals(newName)) { return false; }

        File directory = new File(fileDirectory);
        if (!directory.exists()) { directory.mkdirs(); }

        String fn = file.getName();
        String fileExtension = fn.substring(fn.lastIndexOf("."));
        Path target = FileSystems.getDefault().getPath(
                directory.getAbsolutePath() + File.separator + newName + fileExtension
        );

        try {
            Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(file.toPath());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static File copyExternalFile(File srcFile, String destDirectory, String newFileName) {
        File directory = new File(destDirectory);
        if (!directory.exists()) { directory.mkdirs(); }

        String fn = srcFile.getName();
        String fileExtension = fn.substring(fn.lastIndexOf("."));
        Path target = FileSystems.getDefault().getPath(
                directory.getAbsolutePath() + File.separator + newFileName + fileExtension
        );

        try {
            Files.copy(srcFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return target.toFile();
    }
}
