package org.baldzhiyski.springaiworkshop.multimodal.audio;

import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioGeneration {

    private final OpenAiAudioSpeechModel openAiAudioSpeechModel;

    public AudioGeneration(OpenAiAudioSpeechModel openAiAudioSpeechModel) {
        this.openAiAudioSpeechModel = openAiAudioSpeechModel;
    }

    @GetMapping("/speak")
    public ResponseEntity<byte[]> speak(
            @RequestParam(defaultValue = "It is a great time to start a career in Automotive Industry as a developer") String text
    ){

        var options = OpenAiAudioSpeechOptions.builder()
                .model("tts-1-hd") // or "tts-1-hd" for higher quality
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY) // ALLOY, ECHO, FABLE, ONYX, NOVA, SHIMMER
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f) // 0.25 to 4.0
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(text, options);
        SpeechResponse response = openAiAudioSpeechModel.call(speechPrompt);

        byte[] audioBytes = response.getResult().getOutput();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"speech.mp3\"")
                .body(audioBytes);
    }
}
