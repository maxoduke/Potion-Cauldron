package dev.maxoduke.mods.potioncauldron.config;

import com.google.gson.Gson;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.nio.charset.StandardCharsets;

public class ClientConfig implements IConfig
{
    private final boolean evaporatePotionWhenMixed;
    private final boolean allowMergingPotions;
    private final boolean allowCreatingTippedArrows;

    public ClientConfig(boolean evaporatePotionWhenMixed, boolean allowMergingPotions, boolean allowCreatingTippedArrows)
    {
        this.evaporatePotionWhenMixed = evaporatePotionWhenMixed;
        this.allowMergingPotions = allowMergingPotions;
        this.allowCreatingTippedArrows = allowCreatingTippedArrows;
    }

    public boolean shouldEvaporatePotionWhenMixed() { return evaporatePotionWhenMixed; }

    public boolean shouldAllowMergingPotions() { return allowMergingPotions; }

    public boolean shouldAllowCreatingTippedArrows() { return allowCreatingTippedArrows; }

    public FriendlyByteBuf asBuf()
    {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        String json = new Gson().toJson(this, ClientConfig.class);
        buf.writeByteArray(json.getBytes(StandardCharsets.UTF_8));

        return buf;
    }

    public static ClientConfig fromBuf(FriendlyByteBuf buf)
    {
        return new Gson().fromJson(new String(buf.readByteArray()), ClientConfig.class);
    }
}
