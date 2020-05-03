package com.afterroot.tmdbapi;

import com.afterroot.tmdbapi.model.Artwork;
import com.afterroot.tmdbapi.model.CollectionInfo;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;


public class CollectionsApiTest extends AbstractTmdbApiTest {


    @Test
    public void testGetCollectionInfo() {
        CollectionInfo result = tmdb.getCollections().getCollectionInfo(ID_COLLECTION_STAR_WARS, "");

        assertFalse("No collection information", result.getParts().isEmpty());
    }


    @Test
    public void testGetCollectionImages() throws Exception {
        List<Artwork> result = tmdb.getCollections().getCollectionImages(ID_COLLECTION_STAR_WARS, LANGUAGE_DEFAULT);

        assertFalse("No artwork found", result.isEmpty());
    }
}
