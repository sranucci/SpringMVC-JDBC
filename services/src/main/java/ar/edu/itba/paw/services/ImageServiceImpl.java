package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.recipe.RecipeImage;
import ar.edu.itba.paw.servicesInterface.ImageService;
import ar.edu.itba.persistenceInterface.RecipeImageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {
    private final RecipeImageDao recipeImageDao;

    @Autowired
    public ImageServiceImpl(final RecipeImageDao recipeImageDao) {
        this.recipeImageDao = recipeImageDao;
    }


    @Transactional
    @Override
    public void createImages(long recipeId, List<byte[]> serializedImage) {
        for (int i = 0; i < serializedImage.size(); i++)
            recipeImageDao.createImage(recipeId, serializedImage.get(i), i == 0);
    }


    @Transactional(readOnly = true)
    @Override
    public Optional<RecipeImage> getImage(long imageId) {
        return recipeImageDao.getImage(imageId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Long> getImagesForRecipe(long recipeId) {
        return recipeImageDao.getImagesForRecipe(recipeId);
    }

    @Transactional
    @Override
    public boolean removeImages(long recipeId) {
        return recipeImageDao.removeImages(recipeId);
    }
}
