package com.example.objectpool;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.util.adt.pool.GenericPool;

import com.example.some.MainActivity;

public class ProjectilesPool extends GenericPool<Sprite> {
	private TiledTextureRegion mTextureRegion;

	public ProjectilesPool(TiledTextureRegion pTextureRegion) {
		if (pTextureRegion == null) {
			// Need to be able to create a Sprite so the Pool needs to have a
			// TextureRegion
			throw new IllegalArgumentException(
					"The texture region must not be NULL");
		}
		mTextureRegion = pTextureRegion;
	}

	/** Called when a projectile is required but there isn't one in the pool */
	@Override
	protected Sprite onAllocatePoolItem() {
		return new Sprite(0, 0, mTextureRegion.deepCopy(), MainActivity.vertexBufferObjectManager);
	}

	/** Called when a projectile is sent to the pool */
	protected void onHandleRecycleItem(final Sprite projectile) {
		projectile.clearEntityModifiers();
		projectile.clearUpdateHandlers();
		projectile.setVisible(false);
		projectile.detachSelf();
		projectile.reset();
	}
}