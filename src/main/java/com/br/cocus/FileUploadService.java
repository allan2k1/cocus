package com.br.cocus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileUploadService {

    private final Path fileUploadLocation;

    @Autowired
    public FileUploadService(FileUploadProperties fileUploadProperties){
        this.fileUploadLocation = Paths.get(fileUploadProperties.getUploadDir()).toAbsolutePath().normalize();
    }

    public String oneRandomBackwards () throws IOException {
        return new StringBuilder(getRandomElement(fileContextToList())).reverse().toString();
    }

    public List<String> longest100lines() throws IOException {
        List<String> lines = fileContextToList();

        return getLargestLines(lines, lines.size() < 100 ? lines.size() : 100);
    }

    public List<String> longest20LinesOneFile() throws IOException {

        String randomFile = getRandomElement(getFileNames());
        List<String> lines = fileContextToList(randomFile);

        return getLargestLines(lines, lines.size() < 20 ? lines.size() : 20);
    }

    public FileInformation oneRandomLine () throws IOException {
        FileInformation fileInformation = new FileInformation();
        Map<String, List> uploads = new HashMap<>();

        for (String s : getFileNames()){
            List<String> txt = readFile(s);
            uploads.put(s, txt);
        }

        List keys = List.of(uploads.keySet().toArray());
        String randomKey = getRandomElement(keys);
        String randomLine = getRandomElement(uploads.get(randomKey));

        fileInformation.setContent(randomLine);
        fileInformation.setFileName(randomKey);
        fileInformation.setLineNumber(uploads.get(randomKey).indexOf(randomLine) + 1);
        fileInformation.setFrequentLetter(findFrequentLetter(randomLine));

        return fileInformation;
    }

    public static List<String> getLargestLines(List<String> lines, int n) {
        //Sorts the list based on the length of strings in descending order
        Collections.sort(lines, (s1, s2) -> Integer.compare(s2.length(), s1.length()));

        // Returns the first n elements of the list (the largest strings)
        return lines.subList(0, Math.min(n, lines.size()));
    }

    public static String findFrequentLetter(String str) {
        str = str.replaceAll("\\s+", "");

        Map<Character, Integer> frequency = new HashMap<>();

        // Counts the frequency of each character in the string
        for (char c : str.toCharArray()) {
            frequency.put(c, frequency.getOrDefault(c, 0) + 1);
        }

        char frequentLetter = '\0';
        int maxFrequency = 0;

        // Find the character with the highest frequency
        for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                frequentLetter = entry.getKey();
            }
        }

        return String.valueOf(frequentLetter);
    }

    public String getRandomElement(List<String> list)
    {
        Random rand = new Random();
        return list.get(rand.nextInt(list.size()));
    }

    public List<String> getFileNames() throws IOException {

        // Returns the name of all files that were uploaded
        List<String> fileNames = Files.list(fileUploadLocation)
                .map(Path::getFileName)
                .map(Path::toString).collect(Collectors.toList());

        return fileNames;
    }

    private List<String> readFile(String s) throws IOException {
        // Read from a file to a list
        return Files.readAllLines(Paths.get(fileUploadLocation + "/" + s), Charset.defaultCharset());
    }

    private List<String> fileContextToList(String... files) throws IOException {
        List<String> lines = new ArrayList<>();
        List<String> allFiles = new ArrayList<>(List.of(files));

        // Load the contents of a specific file or all of them into a list
        if (allFiles.size() == 0){
            allFiles = getFileNames();
        }

        for (String s : allFiles){
            List<String> txt = readFile(s);

            for(String t : txt) {
                lines.add(t);
            }
        }

        return lines;
    }
}
