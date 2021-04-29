/*
* This Java file showcases a very rudimentory console based conversation with IBM Watson
*/
package moodra;

import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResult;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import com.ibm.watson.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.text_to_speech.v1.model.CreateVoiceModelOptions;
import com.ibm.watson.text_to_speech.v1.model.SynthesizeOptions;
import com.ibm.watson.text_to_speech.v1.model.VoiceModel;
import com.ibm.watson.text_to_speech.v1.model.Voices;
import com.ibm.watson.text_to_speech.v1.util.WaveUtils;
import java.awt.Toolkit;
import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

/**
 *
 * @authors 814318(HARALD PURNELL), 808703(CP), 732895(JA), 804960(MT) 
 */
public class Moodra {
    
    /**
     * @param args the command line arguments
     */
    static Authenticator authenticatorStT = new IamAuthenticator("<Insert API key here>"); // Service authenticator number
    static SpeechToText serviceStT = new SpeechToText(authenticatorStT);
    static Authenticator authenticatorTtS = new IamAuthenticator("<Insert API key here>"); // Service authenticator number
    static TextToSpeech serviceTtS = new TextToSpeech(authenticatorTtS);
    
    public static void main(String[] args) throws IOException, InterruptedException {

        HashMap<String, String> map = new HashMap<String, String>(); // Changes bit rate for audio files created by IBM Watson allowing them to run on Windows OS
        map.put("accept", "audio/wav;rate=8000");
        serviceTtS.setDefaultHeaders(map);
        
        File audioStart = new File("N:/Sample/greet_alt_3.wav");
        String messageStart = speechText(audioStart);

        if (messageStart.matches(".*Moodra") || messageStart.matches(".*moodra") || messageStart.matches(".*odra") || messageStart.matches("M. O. O. D. R. A.") || messageStart.matches("M. O. O. D. R. A. "))
        {
            Toolkit.getDefaultToolkit().beep(); // Alert user that Moodra is listening

            writeToFile(WaveUtils.reWriteWaveHeader(textSpeech("Hello, what's your name?", "")), new File("output.wav"));
            playAudio("output.wav"); // Moodra
            
            Thread.sleep(1500);
            
            File audioName = new File("N:/Sample/name.wav");
            String messageName = speechText(audioName);
            
            String name = messageName.replaceAll("^.*?(\\w+)\\W*$", "$1");

            writeToFile(WaveUtils.reWriteWaveHeader(textSpeech("Hello ", name)), new File("output.wav")); // Moodra
            playAudio("output.wav");
            
            Thread.sleep(1500);

            writeToFile(WaveUtils.reWriteWaveHeader(textSpeech("Was there anything else I can help you with?", "")), new File("output.wav")); // Moodra
            playAudio("output.wav");
            
            Thread.sleep(1500);

	    // Section written by 804960(MT)
            audioName = new File ("sample/read2.wav"); // Flaw: Three is read as Free
            messageName = speechText(audioName);
            if (messageName.contains("read")) {
                String bookTitle = messageName.replace("read", "");
                writeToFile(WaveUtils.reWriteWaveHeader(textSpeech("Okay, lets read " + bookTitle, "")), new File("output.wav")); // Flaw: Wrong type of "READ" said here.
                bookTitle = bookTitle.replaceAll(" ", "");
                
                System.out.println(bookTitle);
                playAudio("output.wav");
                Thread.sleep(1500);
                readBook(bookTitle);
            }
	    // End of section             

            Path p = Paths.get("N:/Sample/Kalimba.wav");
            String musicName = p.getFileName().toString();
            String nameWithoutExt = FilenameUtils.removeExtension(musicName);
            
            writeToFile(WaveUtils.reWriteWaveHeader(textSpeech("Okay, playing", nameWithoutExt)), new File("output.wav")); // Moodra
            playAudio("output.wav");
          
            Thread.sleep(1500);
            
            File audioMusic = new File("sample/music.wav");
            String messageMusic = speechText(audioMusic);
            //String messageMusic = "play Sleep Away"; 

            if (messageMusic == "play" || messageMusic == "Play" || messageMusic == "PLAY") {
                
                // Error
                writeToFile(WaveUtils.reWriteWaveHeader(textSpeech("I'm sorry, I did not understand that")), new File("output.wav"));
                playAudio("output.wav");
            }
            else if (messageMusic.matches(".*music.*") || messageMusic.matches(".*Music.*") || messageMusic.matches(".*MUSIC.*")){

                // Random song
                Random rand = new Random();
                File folder = new File("music"); // Find directory containing song audio files
                String[] files = folder.list(); // Add all audio file names and extensions to list
                int randomIndex = rand.nextInt(files.length); 

                String randomSongName = files[randomIndex]; // Find random file
                String songNameNoExt = FilenameUtils.removeExtension(randomSongName); // Remove extension from randomly found audio file
                String pathSong = "music/" + randomSongName; // File path for audio song file

                String messageSongName = "Okay, playing" + songNameNoExt;
                writeToFile(WaveUtils.reWriteWaveHeader(textSpeech(messageSongName)), new File("output.wav")); // Moodra
                playAudio("output.wav"); 
                
                Thread.sleep(1500);
                
                playAudio(pathSong);  
            }
            else {
                
                // Specific song
                if (messageMusic.matches(".*song.*") || messageMusic.matches(".*Song.*") || messageMusic.matches(".*SONG.*"))
                {
                    String[] song = createSongFile(messageMusic, "song");
                    String messageSong = song[0];
                    String songPath = song[1];

                    writeToFile(WaveUtils.reWriteWaveHeader(textSpeech(messageSong)), new File("output.wav")); // Moodra
                    playAudio("output.wav"); 
                
                    Thread.sleep(1500);
                
                    playAudio(songPath); 
                }
                else 
                {
                    String[] song = createSongFile(messageMusic, "play");
                    String messageSong = song[0];
                    String songPath = song[1];
                    
                    writeToFile(WaveUtils.reWriteWaveHeader(textSpeech(messageSong)), new File("output.wav")); // Moodra
                    playAudio("output.wav"); 
                
                    Thread.sleep(1500);
                
                    playAudio(songPath); 
                }
            }
        } 
    }
    
    private static void writeToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
    } 
    

    // Section written by 804960(MT)
    public static void readBook(String title) throws InterruptedException{
        try {
            // check first 3 characters of each line for "[B]", then remove it
            File book = new File ("books/" + title + ".txt"); // Placeholder until audio input works
            BufferedReader readBook = new BufferedReader(new FileReader(book));
            String readLine = ""; 
            System.out.println("Reading file");
            while ((readLine = readBook.readLine()) != null){//Check for empty line, if so stop.
                if (readLine.length()>= 1) {
                    System.out.println(readLine);
                    writeToFile(WaveUtils.reWriteWaveHeader(textSpeech(readLine, "")), new File("output.wav"));
                    playAudio("output.wav");
                    Thread.sleep(500);
                    // if (O Pressed)
                    //      place [B] at start of current line
                }
                else{
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    // End of section    

    public static String[] createSongFile(String message, String index)
    {
        message = message.toLowerCase();
        index = index.toLowerCase();
        
        String songName = message.substring(message.lastIndexOf(index) + 4); // Find all words after index, fixed constant 4 (play, song)
        String songTrimmed = songName.replaceAll("^\\s+", ""); // Remove space at start of string
        String songNameUC = capitalize(songTrimmed); // Capatalise first letter of each word in string
        String messageSongName = "Okay, playing" + songNameUC; 
        String musicPath = "music/" + songNameUC + ".wav"; // File path for audio song file 
        
        return new String[]{messageSongName, musicPath}; // Multi-value return
    }
    
    public static void playAudio(String file) {

        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(file)));
            clip.start();
            Thread.sleep(1000);
        }
        catch (Exception exc)
        {
            exc.printStackTrace(System.out);
        }
    }
    
    public static String speechText(File audio) throws FileNotFoundException {
        
        String message = null;
        
        RecognizeOptions options = new RecognizeOptions.Builder()
                .audio(audio)
                .contentType(HttpMediaType.AUDIO_WAV)
                .build();
        SpeechRecognitionResults result = serviceStT.recognize(options).execute().getResult();
        List<SpeechRecognitionResult> transcripts = result.getResults();
        
        for(SpeechRecognitionResult transcript: transcripts){
            message = transcript.getAlternatives().get(0).getTranscript(); 
        }
        
        return message;
    }
    
    public static InputStream textSpeech(String message, String ext) {
        
        SynthesizeOptions synthesizeOptionsMusic = new SynthesizeOptions.Builder().text(message + ext).voice(SynthesizeOptions.Voice.EN_US_MICHAELV3VOICE).build();
        InputStream input = serviceTtS.synthesize(synthesizeOptionsMusic).execute().getResult();
        return input;
    }
    
    
}
    
    




