package com.example.objectpool;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.pool.GenericPool;

import com.example.some.MainActivity;

public class TargetsPool extends GenericPool<AnimatedSprite> {

	private TiledTextureRegion mTextureRegion;

	public TargetsPool(TiledTextureRegion mTargetTextureRegion) {
		if (mTargetTextureRegion == null) {
			throw new IllegalArgumentException(
					"The texture region must not be NULL");
		}
		mTextureRegion = mTargetTextureRegion;
	}

	@Override
	protected AnimatedSprite onAllocatePoolItem() {
		return new AnimatedSprite(0, 0, mTextureRegion.deepCopy(), MainActivity.vertexBufferObjectManager);
	}

	protected void onHandleRecycleItem(final AnimatedSprite target) {
		target.clearEntityModifiers();
		target.clearUpdateHandlers();
		target.setVisible(false);
		target.detachSelf();
		target.reset();
	}

}