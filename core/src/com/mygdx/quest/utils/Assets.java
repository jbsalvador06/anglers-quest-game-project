package com.mygdx.quest.utils;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Assets {

    private AssetManager assetManager = new AssetManager();

    public Assets() {
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
    }

    public static final AssetDescriptor<Skin> SKIN = new AssetDescriptor<>("assets/skins/quest-skin.json", Skin.class, new SkinLoader.SkinParameter("assets/skins/quest-skin.atlas"));

    public static final AssetDescriptor<TiledMap> MAP = new AssetDescriptor<>("assets/maps/map.tmx", TiledMap.class);

    public static final AssetDescriptor<TextureAtlas> PLAYER_TILESET = new AssetDescriptor<>("assets/player/playerMove.atlas", TextureAtlas.class);

    public void loadSkin() {
        assetManager.load(SKIN);
    }

    public void loadMap() {
        assetManager.load(MAP);
    }

    public void loadPlayer() {
        assetManager.load(PLAYER_TILESET);
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public void dispose() {
        assetManager.dispose();
    }

}
