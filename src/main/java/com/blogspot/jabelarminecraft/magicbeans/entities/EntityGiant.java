/**
    Copyright (C) 2014 by jabelar

    This file is part of jabelar's Minecraft Forge modding examples; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
*/

package com.blogspot.jabelarminecraft.magicbeans.entities;

import java.util.Set;

import javax.annotation.Nullable;

import com.blogspot.jabelarminecraft.magicbeans.MagicBeans;
import com.blogspot.jabelarminecraft.magicbeans.ai.EntityGiantAISeePlayer;
import com.blogspot.jabelarminecraft.magicbeans.explosions.GiantAttack;
import com.blogspot.jabelarminecraft.magicbeans.utilities.Utilities;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * @author jabelar
 *
 */
public class EntityGiant extends EntityCreature implements IEntity, IEntityAdditionalSpawnData
{
    protected NBTTagCompound syncDataCompound = new NBTTagCompound();
    protected final BossInfoServer bossInfo = (new BossInfoServer(new TextComponentTranslation("entity.jabelar_giant.name", new Object[0]), BossInfo.Color.PINK, BossInfo.Overlay.PROGRESS));
    protected static final Predicate<EntityPlayerMP> VALID_PLAYER = Predicates.<EntityPlayerMP>and(EntitySelectors.IS_ALIVE, EntitySelectors.<EntityPlayerMP>withinRange(0.0D, 128.0D, 0.0D, 192.0D));
    protected static final SoundEvent SOUND_EVENT_AMBIENT = null;
    protected static final SoundEvent SOUND_EVENT_HURT = new SoundEvent(new ResourceLocation(MagicBeans.MODID+":mob.giant.hurt"));
    protected static final SoundEvent SOUND_EVENT_DEATH = new SoundEvent(new ResourceLocation(MagicBeans.MODID+":mob.giant.death"));

    // good to have instances of AI so task list can be modified, including in sub-classes
    protected EntityAIBase aiSwimming = new EntityAISwimming(this);
    protected EntityAIBase aiAttackOnCollide = new EntityAIAttackMelee(this, 1.0D, true);
    protected EntityAIBase aiMoveTowardsRestriction = new EntityAIMoveTowardsRestriction(this, 1.0D);
    protected EntityAIBase aiMoveThroughVillage = new EntityAIMoveThroughVillage(this, 1.0D, false);
    protected EntityAIBase aiWander = new EntityAIWander(this, 1.0D);
    protected EntityAIBase aiWatchClosest = new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F);
    protected EntityAIBase aiLookIdle = new EntityAILookIdle(this);
    protected EntityAIBase aiHurtByTarget = new EntityAIHurtByTarget(this, true);
    protected EntityAIBase aiSeePlayer = new EntityGiantAISeePlayer(this);

    // fields related to being attacked
    protected Entity entityAttackedBy = null;
    protected boolean wasDamageDoneOutsideResistancePeriod = true;
    protected DamageSource damageSource = null;
    protected float damageAmount = 0;
    
    // fields related to attacking
    protected Entity entityAttacked = null;
    protected float attackDamage = 1.0F;
    protected int knockback = 0;
    protected int respiration = 0;
	protected boolean wasDamageDone = false;
	protected GiantAttack specialAttack;
    
	/**
	 * @param parWorld
	 */
	public EntityGiant(World parWorld) 
	{
		super(parWorld);
		
		setupAI();
		setSize(1.0F, 4.5F);
        isImmuneToFire = true ;
		specialAttack = new GiantAttack(this, 12);
	}

	// you don't have to call this as it is called automatically during EntityLiving subclass creation
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes(); 

		// standard attributes registered to EntityLivingBase
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MagicBeans.configGiantHealth);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D); 
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.9D); // hard to knock back
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);

	    // need to register any additional attributes
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(MagicBeans.configGiantAttackDamage);
	}
	
    public void updateProgressBar()
    {
        bossInfo.setPercent(getHealth() / getMaxHealth());
        Set<EntityPlayerMP> set = Sets.<EntityPlayerMP>newHashSet();

        for (EntityPlayerMP entityplayermp : this.worldObj.getPlayers(EntityPlayerMP.class, VALID_PLAYER))
        {
            this.bossInfo.addPlayer(entityplayermp);
            set.add(entityplayermp);
        }

        Set<EntityPlayerMP> set1 = Sets.newHashSet(this.bossInfo.getPlayers());
        set1.removeAll(set);

        for (EntityPlayerMP entityplayermp1 : set1)
        {
            this.bossInfo.removePlayer(entityplayermp1);
        }
    }

	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		// regen
		if (ticksExisted%50 == 0 && this.isEntityAlive())
		{
			setHealth(getHealth()+1);
		}
		
		updateProgressBar();
		
        // create particles
        if (ticksExisted % 5 == 0)
        {
            MagicBeans.proxy.generateMysteriousParticles(this);
        }
		
		// falling on death can damage like special attack
		if (deathTime == 19) // time this to point in RenderGiant death fall sequence when it hits the ground
		{
			getSpecialAttack().doGiantAttack(MagicBeans.configGiantAttackDamage*3);
		}
	}

	/**
     * Causes this entity to do an upwards motion (jumping).
     */
    @Override
	public void jump()
    {
        motionY = 0.41999998688697815D*1.5;
        isAirBorne = true;
        ForgeHooks.onLivingJump(this);
    }	

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    @Override
    public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, @Nullable ItemStack stack, EnumHand hand)
    {
        collideWithNearbyEntities();        // check if have already spawned castle
        return EnumActionResult.SUCCESS;
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#setupAI()
	 */
	@Override
	public void setupAI() 
	{
//        getNavigator().setBreakDoors(true);
        clearAITasks();
        tasks.addTask(0, aiSwimming);
        tasks.addTask(1, aiAttackOnCollide);
        tasks.addTask(2, aiMoveTowardsRestriction);
        tasks.addTask(3, aiMoveThroughVillage);
        tasks.addTask(4, aiWander);
        tasks.addTask(5, aiWatchClosest);
        tasks.addTask(6, aiLookIdle);
        targetTasks.addTask(0, aiSeePlayer);
	}

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#clearAITasks()
	 */
	@Override
	public void clearAITasks() 
	{
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
	}
	
    @Override
	public boolean attackEntityAsMob(Entity parEntity)
    {
    	//DEBUG
    	// System.out.println("EntityGiant attackEntityAsMob");
    	
    	entityAttacked = parEntity;
        attackDamage = (float)getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        knockback = 0;

        if (entityAttacked instanceof EntityLivingBase)
        {
            attackDamage += EnchantmentHelper.getModifierForCreature(getHeldItem(swingingHand), ((EntityLivingBase)parEntity).getCreatureAttribute());
            knockback += EnchantmentHelper.getRespirationModifier(this); // the getRespiration() method is mis-named, it is getKnockback.
        }

        wasDamageDone = entityAttacked.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage);
        // DEBUG
        // System.out.println("Damage was done ="+wasDamageDone+", damage amount ="+attackDamage);
        if (wasDamageDone)
        {
            if (rand.nextInt(10) < 2)
            {
            	playSound(new SoundEvent(new ResourceLocation(MagicBeans.MODID+":mob.giant.attack")), getSoundVolume(), getSoundPitch());
            }

            if (knockback > 0)
            {
            	entityAttacked.addVelocity(-MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F, 0.1D, MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F);
                motionX *= 0.6D;
                motionZ *= 0.6D;
            }

            int fireModifier = EnchantmentHelper.getFireAspectModifier(this);

            if (fireModifier > 0)
            {
            	entityAttacked.setFire(fireModifier * 4);
            }

            if (entityAttacked instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)entityAttacked;
                ItemStack itemstack = getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

                if (itemstack != null && itemstack1 != null && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD)
                {
                    float f1 = 0.25F + EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

                    if (this.rand.nextFloat() < f1)
                    {
                        entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                        this.worldObj.setEntityState(entityplayer, (byte)30);
                    }
                }
            }

            applyEnchantments(this, entityAttacked);
        }
                
        return wasDamageDone;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
	public boolean attackEntityFrom(DamageSource parDamageSource, float parDamageAmount)
    {
    	damageSource = parDamageSource;
    	damageAmount = parDamageAmount;
        entityAttackedBy = damageSource.getEntity();

//        // DEBUG
//    	System.out.println("EntityGiant attackEntityFrom()");
    	
        if (!ForgeHooks.onLivingAttack(this, damageSource, damageAmount)) // in 1.8 this hook returns opposite of 1.7.10
        {
        	// DEBUG
        	System.out.println("LivingAttackEvent must have been canceled");
        	return false;
        }

        // DEBUG
        System.out.println("OnLivingAttack event was not canceled");
        
        if (conditionsPreventDamage(damageSource))
        {
//            // DEBUG
//        	System.out.println("There are conditions preventing damage");
        	
        	return false;
        }

        wasDamageDoneOutsideResistancePeriod = processDamage(damageSource, damageAmount);
               
        updateEntityState();
                
        // process death
        if (getHealth() <= 0.0F)
        {
        	onDeath(damageSource);
        	if (entityAttackedBy instanceof EntityPlayer)
        	{
        		((EntityPlayer)entityAttackedBy).addStat(MagicBeans.achievementGiantSlayer, 1);
        	}
        }
        
        playHurtOrDeathSound();

		return wasDamageDoneOutsideResistancePeriod;
    }

	private void updateEntityState() 
	{
        // flail limbs when attacked
        limbSwingAmount = 1.5F;

        entityAge = 0; // reset despawn counter if you've been attacked by something

        if (entityAttackedBy != null)
        {
        	// DEBUG
        	System.out.println("Attacked by an entity");
        	
            if (entityAttackedBy instanceof EntityLivingBase)
            {
                setRevengeTarget((EntityLivingBase)entityAttackedBy);
            }

            if (entityAttackedBy instanceof EntityPlayer)
            {
            	// DEBUG
            	// System.out.println("Attacked by a player");
                recentlyHit = 100;
                attackingPlayer = (EntityPlayer)entityAttackedBy;
            }
            else if (entityAttackedBy instanceof EntityTameable)
            {
                EntityTameable entityTameable = (EntityTameable)entityAttackedBy;

                if (entityTameable.isTamed())
                {
                    recentlyHit = 100;
                    attackingPlayer = null;
                }
            }
        }

        if (wasDamageDoneOutsideResistancePeriod)
        {
            worldObj.setEntityState(this, (byte)2);

            if (damageSource != DamageSource.drown)
            {
                setBeenAttacked();
            }

            if (entityAttackedBy != null)
            {
                double d1 = entityAttackedBy.posX - posX;
                double d0;

                for (d0 = entityAttackedBy.posZ - posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                {
                    d1 = (Math.random() - Math.random()) * 0.01D;
                }

                attackedAtYaw = (float)(Math.atan2(d0, d1) * 180.0D / Math.PI) - rotationYaw;
                knockBack(entityAttackedBy, damageAmount, d1, d0);
            }
            else // non-entity was damage source
            {
            	// DEBUG
            	// System.out.println("Damage was done by something other than an entity");
                attackedAtYaw = (int)(Math.random() * 2.0D) * 180;
            }
        }
        
        if (getRidingEntity() != entityAttackedBy && getRidingEntity() != entityAttackedBy)
          {
              if (entityAttackedBy != this)
              {
                  this.setAttackTarget((EntityLivingBase) entityAttackedBy);
              }
          }
	}

	/**
	 * @param wasDamageDoneOutsideResistancePeriod
	 */
	private void playHurtOrDeathSound() 
	{
        SoundEvent soundName;

        if (getHealth() <= 0.0F)
        {
            soundName = getDeathSound();
        }
        else
        {
        	if (rand.nextInt(10) <= 1) // annoying if he grunts on every hit
        	{
            	soundName = getHurtSound();
        	}
        	else
        	{
        		soundName = null;
        	}
        }

        if (wasDamageDoneOutsideResistancePeriod && soundName != null)
        {
            playSound(soundName, getSoundVolume(), getSoundPitch());
        }
	}
	
    /**
     * Gets the pitch of living sounds in living entities.
     */
    @Override
	protected float getSoundPitch()
    {
        return (rand.nextFloat() - rand.nextFloat()) * 0.2F + 0.75F;  // makes sound lower than asset
    }


    /**
     * Get number of ticks, at least during which the living entity will be silent.
     */
    @Override
	public int getTalkInterval()
    {
        return 20*15; // quiet for at least 15 seconds
    }

    /**
     * Returns the sound this mob makes while it's alive.
     */
    @Override
	protected SoundEvent getAmbientSound()
    {
        return SOUND_EVENT_AMBIENT;
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    @Override
	protected SoundEvent getHurtSound()
    {
        return SOUND_EVENT_HURT;
    }

    /**
     * Returns the sound this mob makes on death.
     */
    @Override
	protected SoundEvent getDeathSound()
    {
        return SOUND_EVENT_DEATH;
    }

	/**
	 * @param parDamageSource
	 * @param parDamageAmount
	 * @return
	 */
	private boolean processDamage(DamageSource parDamageSource,
			float parDamageAmount) 
	{       
        // Check if helmet protects from anvil or falling block, and damage helmet
        if ((damageSource == DamageSource.anvil || damageSource == DamageSource.fallingBlock) && getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null)
        {
            getItemStackFromSlot(EntityEquipmentSlot.HEAD).damageItem((int)(damageAmount * 4.0F + rand.nextFloat() * damageAmount * 2.0F), this);
            damageAmount *= 0.75F;
        }

        wasDamageDoneOutsideResistancePeriod = true;

        // Check for hurt resistance
        if (hurtResistantTime > maxHurtResistantTime / 2.0F)
        {
        	// DEBUG
        	System.out.println("Reduced damage done, damage amount ="+damageAmount+". health remaining ="+getHealth());
            if (damageAmount <= lastDamage)
            {
                return false;
            }

            damageEntity(damageSource, damageAmount - lastDamage);
            lastDamage = damageAmount;
            wasDamageDoneOutsideResistancePeriod = false;
        }
        else // do normal damage
        {
            lastDamage = damageAmount;
//            prevHealth = getHealth();
            hurtResistantTime = maxHurtResistantTime;
            damageEntity(damageSource, damageAmount);
            hurtTime = maxHurtTime = 10;
        	// DEBUG
        	System.out.println("Normal damage done, damage amount ="+damageAmount+". health remaining ="+getHealth());
        }
        
		return wasDamageDoneOutsideResistancePeriod;
	}

	/**
	 * @param parDamageSource
	 * @return
	 */
	private boolean conditionsPreventDamage(DamageSource parDamageSource) 
	{
        // Only process damage on server side
        if (worldObj.isRemote)
        {
            return true;
        }

        // Check for invulnerability
        if (isEntityInvulnerable(parDamageSource))
        {
            return true;
        }

        // Check if already dead
        if (getHealth() <= 0.0F)
        {
            return true;
        }

        // Check if damage is from fire but resistant
        if (damageSource.isFireDamage() && isPotionActive(MobEffects.FIRE_RESISTANCE))
        {
            return true;
        }
        
		return false;
	}

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    @Override
	protected void damageEntity(DamageSource parDamageSource, float parDamageAmount)
    {
    	// DEBUG
    	System.out.println("EntityGiant damageEntity()");
    	
        if (!isEntityInvulnerable(parDamageSource))
        {
            parDamageAmount = ForgeHooks.onLivingHurt(this, parDamageSource, parDamageAmount);
            if (parDamageAmount <= 0) return;
            parDamageAmount = applyArmorCalculations(parDamageSource, parDamageAmount);
            parDamageAmount = applyPotionDamageCalculations(parDamageSource, parDamageAmount);
            float f1 = parDamageAmount;
            parDamageAmount = Math.max(parDamageAmount - getAbsorptionAmount(), 0.0F);
            setAbsorptionAmount(getAbsorptionAmount() - (f1 - parDamageAmount));

            if (parDamageAmount != 0.0F)
            {
                float f2 = getHealth();
                setHealth(f2 - parDamageAmount);
                getCombatTracker().trackDamage(parDamageSource, f2, parDamageAmount);
                setAbsorptionAmount(getAbsorptionAmount() - parDamageAmount);
            }
        }
    }

    /**
     * Reduces damage, depending on armor
     */
    @Override
	protected float applyArmorCalculations(DamageSource parDamageSource, float parDamageAmount)
    {
        if (!parDamageSource.isUnblockable())
        {
            int i = 25 - getTotalArmorValue();
            float f1 = parDamageAmount * i;
            damageArmor(parDamageAmount);
            parDamageAmount = f1 / 25.0F;
        }

        return parDamageAmount;
    }

    /**
     * Returns the current armor value as determined by a call to InventoryPlayer.getTotalArmorValue
     */
    @Override
	public int getTotalArmorValue() // like he's wearing set of iron armor
    {
        int totalArmorValue = Items.IRON_CHESTPLATE.damageReduceAmount+Items.IRON_HELMET.damageReduceAmount+Items.IRON_LEGGINGS.damageReduceAmount+Items.IRON_BOOTS.damageReduceAmount;
        return totalArmorValue;
    }
    
    @Override
	public Item getDropItem()
    {
    	// DEBUG
    	System.out.println("Giant getDropItem() called");
    	
    	ItemArmor itemToDrop = MagicBeans.bootsOfSafeFalling;
		return itemToDrop;    	
    }

    @Override
	protected void dropFewItems(boolean parRecentlyHitByPlayer, int parlootingLevel)
    {
    	// DEBUG
    	System.out.println("Dropping Giant loot");
    	
    	dropItem(MagicBeans.bootsOfSafeFalling, 1);
    	// dropItem(MagicBeans.leggingsOfSafeFalling, 1);
    	// dropItem(MagicBeans.chestplateOfSafeFalling, 1);
    	// dropItem(MagicBeans.helmetOfSafeFalling, 1);
    }

    @Override
	public float getEyeHeight()
    {
        return height * 0.85F ;
    }
    
    @Override
    public void setDead()
    {
//    	// DEBUG
//    	System.out.println("Giant has died");
    	super.setDead();
    }
    
    public GiantAttack getSpecialAttack()
    {
    	return specialAttack;
    }

    @Override
	public NBTTagCompound getSyncDataCompound()
    {
    	return syncDataCompound;
    }
    
    @Override
	public void setSyncDataCompound(NBTTagCompound parCompound)
    {
    	syncDataCompound = parCompound;
    }
    
    @Override
    public void readEntityFromNBT(NBTTagCompound parCompound)
    {
    	super.readEntityFromNBT(parCompound);
    	syncDataCompound = (NBTTagCompound) parCompound.getTag("syncDataCompound");
        // DEBUG
        System.out.println("EntityGiant readEntityFromNBT");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound parCompound)
    {
    	super.writeEntityToNBT(parCompound);
    	parCompound.setTag("syncDataCompound", syncDataCompound);
        // DEBUG
        System.out.println("EntityGiant writeEntityToNBT");
    }
    
	@Override
	public void sendEntitySyncPacket()
	{
		Utilities.sendEntitySyncPacketToClient(this);
	}

	/* 
	 * This is where you initialize any custom fields for the entity to ensure client and server are synced
	 */
	@Override
	public void initSyncDataCompound() 
	{
		// don't use setters because it might be too early to send sync packet
		syncDataCompound.setFloat("scaleFactor", 2.25F);
		syncDataCompound.setInteger("specialAttackTimer", 0);
	}
    
	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#setScaleFactor(float)
	 */
    @Override
    public void setScaleFactor(float parScaleFactor)
    {
        syncDataCompound.setFloat("scaleFactor", Math.abs(parScaleFactor));
       
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

	/* (non-Javadoc)
	 * @see com.blogspot.jabelarminecraft.magicbeans.entities.IEntityMagicBeans#getScaleFactor()
	 */
    @Override
    public float getScaleFactor()
    {
        return syncDataCompound.getFloat("scaleFactor");
    }
    
    public void setSpecialAttackTimer(int parSpecialAttackTimer)
    {
        syncDataCompound.setInteger("specialAttackTimer", parSpecialAttackTimer);
       
        // DEBUG
        System.out.println("Setting special attack timer to "+parSpecialAttackTimer);
        
        // don't forget to sync client and server
        sendEntitySyncPacket();
    }

    public int getSpecialAttackTimer()
    {
        return syncDataCompound.getInteger("specialAttackTimer");
    }
    
    public void decrementSpecialAttackTimer()
    {
    	// DEBUG
    	System.out.println("Decrementing special attack timer");
    	int timer = getSpecialAttackTimer() - 1;
    	if (timer < 0)
    	{
    		timer = 0;
    	}
    	
    	setSpecialAttackTimer(timer);

    }
    
	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData#writeSpawnData(io.netty.buffer.ByteBuf)
	 */
	@Override
	public void writeSpawnData(ByteBuf parBuffer) 
	{
		initSyncDataCompound();
		ByteBufUtils.writeTag(parBuffer, syncDataCompound);		
	}


	/* (non-Javadoc)
	 * @see net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData#readSpawnData(io.netty.buffer.ByteBuf)
	 */
	@Override
	public void readSpawnData(ByteBuf parBuffer) 
	{
		syncDataCompound = ByteBufUtils.readTag(parBuffer);	
		// DEBUG
		System.out.println("EntityGiant spawn data received, scaleFactor = "+getScaleFactor());
	}

	public Block findBlockUnderEntity(Entity parEntity)
	{
	    int blockX = MathHelper.floor_double(parEntity.posX);
	    int blockY = MathHelper.floor_double(parEntity.getEntityBoundingBox().minY)-1;
	    int blockZ = MathHelper.floor_double(parEntity.posZ);
	    return parEntity.worldObj.getBlockState(new BlockPos(blockX, blockY, blockZ)).getBlock();
	}

    /**
     * Returns false if this Entity is a boss, true otherwise.
     */
    @Override
    public boolean isNonBoss()
    {
        return false; // giant is a boss
    }

}

