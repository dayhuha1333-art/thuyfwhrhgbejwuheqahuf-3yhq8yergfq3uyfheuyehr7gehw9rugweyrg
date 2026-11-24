package meldexun.nothirium;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "func_175068_a", at = @At("RETURN"))
    private void onRenderWorld(float partialTicks, CallbackInfo ci) {
        if (!Nothirium.xrayEnabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        double px = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * partialTicks;
        double py = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * partialTicks;
        double pz = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * partialTicks;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(2.0F);
        GlStateManager.translate(-px, -py, -pz);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        ChunkPos pChunk = new ChunkPos(mc.player.getPosition());
        int radius = 4;

        for (int cx = pChunk.x - radius; cx <= pChunk.x + radius; cx++) {
            for (int cz = pChunk.z - radius; cz <= pChunk.z + radius; cz++) {
                if (!mc.world.isChunkGeneratedAt(cx, cz)) continue;
                Chunk chunk = mc.world.getChunk(cx, cz);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int y = 0; y < 256; y++) {
                            BlockPos pos = new BlockPos(chunk.x * 16 + x, y, chunk.z * 16 + z);
                            IBlockState state = chunk.getBlockState(pos);
                            Block block = state.getBlock();
                            String id = block.getRegistryName().toString();
                            if (!Nothirium.blockColors.containsKey(id)) continue;

                            int color = Nothirium.blockColors.get(id);
                            float r = ((color >> 16) & 0xFF) / 255f;
                            float g = ((color >> 8) & 0xFF) / 255f;
                            float b = (color & 0xFF) / 255f;
                            float a = 0.6f;

                            double x1 = pos.getX(), y1 = pos.getY(), z1 = pos.getZ();
                            double x2 = x1 + 1, y2 = y1 + 1, z2 = z1 + 1;

                            drawLine(buffer, x1, y1, z1, x2, y1, z1, r, g, b, a);
                            drawLine(buffer, x2, y1, z1, x2, y1, z2, r, g, b, a);
                            drawLine(buffer, x2, y1, z2, x1, y1, z2, r, g, b, a);
                            drawLine(buffer, x1, y1, z2, x1, y1, z1, r, g, b, a);
                            drawLine(buffer, x1, y2, z1, x2, y2, z1, r, g, b, a);
                            drawLine(buffer, x2, y2, z1, x2, y2, z2, r, g, b, a);
                            drawLine(buffer, x2, y2, z2, x1, y2, z2, r, g, b, a);
                            drawLine(buffer, x1, y2, z2, x1, y2, z1, r, g, b, a);
                            drawLine(buffer, x1, y1, z1, x1, y2, z1, r, g, b, a);
                            drawLine(buffer, x2, y1, z1, x2, y2, z1, r, g, b, a);
                            drawLine(buffer, x2, y1, z2, x2, y2, z2, r, g, b, a);
                            drawLine(buffer, x1, y1, z2, x1, y2, z2, r, g, b, a);
                        }
                    }
                }
            }
        }

        tessellator.draw();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void drawLine(BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float a) {
        buffer.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        buffer.pos(x2, y2, z2).color(r, g, b, a).endVertex();
    }
}
