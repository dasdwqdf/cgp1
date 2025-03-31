package game.sound;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {

    private Clip clip;
    private FloatControl volumeControl; // Adiciona controle de volume
    private URL[] soundURL = new URL[30];

    public Sound() {
        soundURL[0] = getClass().getResource("/sound/menu.wav");
        soundURL[1] = getClass().getResource("/sound/battle.wav");
        soundURL[2] = getClass().getResource("/sound/select.wav");
        soundURL[3] = getClass().getResource("/sound/enter.wav");
        soundURL[4] = getClass().getResource("/sound/damage.wav");
        soundURL[5] = getClass().getResource("/sound/mana.wav");
        soundURL[6] = getClass().getResource("/sound/heal.wav");
        soundURL[7] = getClass().getResource("/sound/powerUp.wav");
        soundURL[8] = getClass().getResource("/sound/powerDown.wav");
        soundURL[9] = getClass().getResource("/sound/placeCard.wav");
    }

    public void setFile(int i) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // Obtém o controle de volume
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        clip.start();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        clip.stop();
    }

    // Novo método para ajustar o volume
    public void setVolume(float volume) {
        if (volumeControl != null) {
            // Garante que o volume esteja dentro dos limites permitidos
            float min = volumeControl.getMinimum(); // Normalmente -80.0f
            float max = volumeControl.getMaximum(); // Normalmente 6.0f
            volume = Math.max(min, Math.min(volume, max)); // Restringe o valor ao intervalo permitido

            volumeControl.setValue(volume); // Aplica o volume
        }
    }

    // Método para obter o volume atual
    public float getVolume() {
        if (volumeControl != null) {
            return volumeControl.getValue();
        }
        return 0f; // Retorna 0 como padrão
    }
}
