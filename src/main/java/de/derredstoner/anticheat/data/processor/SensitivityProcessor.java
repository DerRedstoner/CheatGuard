package de.derredstoner.anticheat.data.processor;

import de.derredstoner.anticheat.data.PlayerData;
import de.derredstoner.anticheat.packet.wrapper.client.WrappedPacketPlayInFlying;
import de.derredstoner.anticheat.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class SensitivityProcessor {

    private final PlayerData data;

    public double sensitivity;
    public int sensitivityPercentage;

    private float lastPitch;
    private List<Float> pitchSamples = new ArrayList<>();

    public SensitivityProcessor(PlayerData data) {
        this.data = data;
    }

    public void process(Object e) {
        if(e instanceof WrappedPacketPlayInFlying) {
            WrappedPacketPlayInFlying wrapper = (WrappedPacketPlayInFlying) e;

            float pitch = wrapper.getPitch();

            if(wrapper.isRotating()) {
                float pitchdiff = Math.abs(pitch - lastPitch);
                lastPitch = pitch;

                if(pitch == 0 || Math.abs(pitch) > 90.0 - pitchdiff) {
                    return;
                }
                if(Math.abs(pitchdiff) >= 10.0 || Math.abs(pitchdiff) < 0.05) {
                    return;
                }

                pitchSamples.add(pitchdiff);
                if(pitchSamples.size() >= 100) {
                    float gcd = MathUtil.gcdRational(pitchSamples);
                    double sens = (Math.cbrt(gcd / 8 / 0.15) - 0.2) / 0.6;
                    int sensitivityPercentage = (int) ((sens * 200.0) + 0.0075);

                    if(sensitivityPercentage != -66) {
                        this.sensitivity = sens;
                        this.sensitivityPercentage = sensitivityPercentage;
                    }

                    pitchSamples.clear();
                }
            }
        }
    }

}
