package ar.edu.itba.paw.persistence;

import java.time.Instant;
import java.util.Date;

public abstract class GlobalTestVariables {

    //tbl_user
    protected static final String EMAIL = "mail@maill.com";
    protected static final String EMAIL2 = "mail2@maill.com";
    protected static final String PASSWORD = "password";
    protected static final String PASSWORD2 = "password2";
    protected static final String FIRST_NAME = "first name";
    protected static final String FIRST_NAME2 = "firstt";
    protected static final String LAST_NAME = "last name";
    protected static final String LAST_NAME2 = "last name";
    protected static final boolean IS_ADMIN = false;
    protected static final long USER_ID = 1;
    protected static final long NON_EXISTENT_USER_ID = 0;


    //tbl_recipe
    protected static final long RECIPE_ID = 1;
    protected static final long NON_EXISTENT_RECIPE_ID = 0;

    protected static final String RECIPE_TITLE = "title";
    protected static final String DESCRIPTION = "description";
    protected static final boolean IS_PRIVATE = false;
    protected static final int TOTAL_MINUTES = 75;
    protected static final int MINUTES = 15;
    protected static final int HOURS = 1;
    protected static final int DIFFICULTY = 1;
    protected static final int SERVINGS = 4;
    protected static final Instant DATE = Instant.now();
    protected static final String INSTRUCTIONS = "Instruction 1#Instruction 2#Ins3";
    protected static final String[] INSTRUCTIONS_ARRAY = {"Instruction 1", "Instruction 2", "Ins3"};

    //tbl_ingredient
    protected static final String INGREDIENT_STR_1 = "salt";
    protected static final long INGREDIENT_ID_1 = 1;
    protected static final String INGREDIENT_STR_2 = "salt";
    protected static final long INGREDIENT_ID_2 = 2;


    //tbl_units
    protected static final String UNIT_STR_1 = "g";
    protected static final long UNIT_ID_1= 1;
    protected static final String UNIT_STR_2 = "ml";
    protected static final long UNIT_ID_2= 2;


    //tbl_category
    protected static final String CATEGORY_STR_1 = "Breakfast&brunch";
    protected static final long CATEGORY_ID_1= 1;
protected static final String CATEGORY_STR_2 = "Lunch";
    protected static final long CATEGORY_ID_2= 2;

    protected static final byte[] IMG_BYTEA= new byte[]{0x00};

}
