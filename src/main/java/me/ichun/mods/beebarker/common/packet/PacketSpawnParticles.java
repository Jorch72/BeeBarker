package me.ichun.mods.beebarker.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.beebarker.client.fx.ParticleBuzz;
import me.ichun.mods.beebarker.common.core.EventHandlerServer;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSpawnParticles extends AbstractPacket
{
    public int entityId;
    public int playerId;
    public boolean isSmokeParticles;

    public PacketSpawnParticles(){}

    public PacketSpawnParticles(int entityId, int playerId, boolean isSmokeParticles)
    {
        this.entityId = entityId;
        this.playerId = playerId;
        this.isSmokeParticles = isSmokeParticles;
    }

    @Override
    public void writeTo(ByteBuf buffer)
    {
        buffer.writeInt(entityId);
        buffer.writeInt(playerId);
        buffer.writeBoolean(isSmokeParticles);
    }

    @Override
    public void readFrom(ByteBuf buffer)
    {
        entityId = buffer.readInt();
        playerId = buffer.readInt();
        isSmokeParticles = buffer.readBoolean();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient();
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if(entityId == -1)
        {
            Entity ply = mc.world.getEntityByID(playerId);
            if(ply instanceof EntityLivingBase && !(ply == mc.getRenderViewEntity() && mc.gameSettings.thirdPersonView == 0))
            {
                EntityLivingBase living = (EntityLivingBase)ply;
                double d0 = living.world.rand.nextGaussian() * 0.02D;
                double d1 = living.world.rand.nextGaussian() * 0.02D;
                double d2 = living.world.rand.nextGaussian() * 0.02D;
                Vec3d look = living.getLookVec();
                Vec3d pos = living.getPositionVector().addVector(look.x * 0.5D - look.z * (living.width * 0.67D), look.y * 0.5D + (living.getEyeHeight() * 0.8D), look.z * 0.5D + look.x * (living.width * 0.67D));
                living.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.x, pos.y, pos.z, d0, d1, d2);
            }
        }
        else
        {
            Entity ent = mc.world.getEntityByID(entityId);
            if(ent != null)
            {
                for(int i = 0; i < 7; ++i)
                {
                    double d0 = mc.world.rand.nextGaussian() * 0.02D;
                    double d1 = mc.world.rand.nextGaussian() * 0.02D;
                    double d2 = mc.world.rand.nextGaussian() * 0.02D;
                    if(isSmokeParticles)
                    {
                        mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, ent.posX + (double)(mc.world.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, ent.posY + 0.5D + (double)(mc.world.rand.nextFloat() * ent.height), ent.posZ + (double)(mc.world.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, d0, d1, d2);
                    }
                    else
                    {
                        ent.getEntityData().setBoolean(EventHandlerServer.BARKABLE_STRING, true);
                        mc.effectRenderer.addEffect(new ParticleBuzz(mc.world, ent.posX + (double)(mc.world.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, ent.posY + 0.5D + (double)(mc.world.rand.nextFloat() * ent.height), ent.posZ + (double)(mc.world.rand.nextFloat() * ent.width * 2.0F) - (double)ent.width, d0, d1, d2));
                    }
                }
            }
            Entity ply = mc.world.getEntityByID(playerId);
            if(ply instanceof EntityLivingBase)
            {
                ((EntityLivingBase)ply).swingArm(EnumHand.MAIN_HAND);
            }
        }
    }
}
