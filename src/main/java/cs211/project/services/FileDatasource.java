package cs211.project.services;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class FileDatasource<T> implements Datasource<T> {
    private String directoryName;
    private String fileName;

    public FileDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        checkFileIsExisted();
    }

    private void checkFileIsExisted() {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = getFile();
        if (!file.exists()) {
            BufferedWriter buffer = null;
            try {
                file.createNewFile();
                buffer = createBufferedWriter();
                buffer.write("[]");
                buffer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    buffer.flush();
                    buffer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private File getFile() { return new File(directoryName + File.separator + fileName); }

    protected BufferedReader createBufferedReader() {
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(getFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream,
                StandardCharsets.UTF_8
        );
        return new BufferedReader(inputStreamReader);
    }

    protected BufferedWriter createBufferedWriter() {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(getFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                fileOutputStream,
                StandardCharsets.UTF_8
        );

        return new BufferedWriter(outputStreamWriter);
    }

}