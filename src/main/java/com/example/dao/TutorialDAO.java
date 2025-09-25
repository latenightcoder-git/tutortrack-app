package com.example.dao;

import com.example.exceptions.DatabaseOperationException;
import com.example.exceptions.TutorialNotFoundException;
import com.example.model.Tutorial;

import java.util.ArrayList;

public interface TutorialDAO {
    void addTutorial(Tutorial tutorial) throws DatabaseOperationException;
    Tutorial getTutorialById(int id) throws TutorialNotFoundException, DatabaseOperationException;
    ArrayList<Tutorial> getAllTutorials() throws DatabaseOperationException;
    void updateTutorial(Tutorial tutorial) throws TutorialNotFoundException, DatabaseOperationException;
    void deleteTutorial(int id) throws TutorialNotFoundException, DatabaseOperationException;
}