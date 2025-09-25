package com.example.dao;

import com.example.exceptions.DatabaseOperationException;
import com.example.exceptions.TutorialNotFoundException;
import com.example.model.Tutorial;
import com.example.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;

import java.time.LocalDate;

public class TutorialDAOImpl implements TutorialDAO {
    // Purpose: Converts a row from the database (ResultSet) into a Tutorial object.
    private Tutorial extractTutorialFromResultSet(ResultSet rs) throws SQLException {
        Tutorial tutorial = new Tutorial();
        tutorial.setId(rs.getInt("tutorial_id"));
        tutorial.setTitle(rs.getString("title"));
        tutorial.setAuthor(rs.getString("author"));
        tutorial.setUrl(rs.getString("url"));
        Date sqlDate = rs.getDate("published_date");
        if (sqlDate != null) {
            tutorial.setPublishedDate(sqlDate.toLocalDate());
        }
        return tutorial;
    }

    /*
      SQL: We define the INSERT SQL command with placeholders (?).
      Connection: We open a connection using DBConnection.getConnection().
      PreparedStatement: We prepare the statement and pass the columns we want to return (tutorial_id).
      Set Parameters: We pass in the Tutorial data into the statement.
      Execute: We run the INSERT command.
      Check Rows: If no rows are affected, throw an error.
      Get Generated ID: We retrieve the newly generated tutorial_id and set it in the Tutorial object.
      Cleanup: We close resources in the finally block.
    */
    @Override
    public void addTutorial(Tutorial tutorial) throws DatabaseOperationException {
        String SQL = "INSERT INTO tutorials (title, author, url, published_date) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(SQL, new String[] { "tutorial_id" });

            preparedStatement.setString(1, tutorial.getTitle());
            preparedStatement.setString(2, tutorial.getAuthor());
            preparedStatement.setString(3, tutorial.getUrl());
            preparedStatement.setDate(4, tutorial.getPublishedDate() != null
                    ? Date.valueOf(tutorial.getPublishedDate())
                    : null
            );

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new DatabaseOperationException("Creating tutorial failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tutorial.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating tutorial failed, no ID obtained.");
                }
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error adding tutorial: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { IO.println("Failed to close PreparedStatement in getTutorialById: " + e.getMessage());}
            }
        }
    }


    //Purpose: Retrieves a Tutorial from the database using its ID.
    /*  Prepare a SELECT query to fetch a tutorial by ID.
        Execute the query.
        If a result is found : Convert the row to a Tutorial object using extractTutorialFromResultSet.
        If not found : Throw a TutorialNotFoundException.
        Handle all exceptions and close resources properly.
     */
    @Override
    public Tutorial getTutorialById(int id) throws TutorialNotFoundException, DatabaseOperationException {
        String SQL = "SELECT * FROM tutorials WHERE tutorial_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, id);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractTutorialFromResultSet(resultSet);
            } else {
                throw new TutorialNotFoundException("Tutorial with ID " + id + " not found.");
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving tutorial by ID: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
            if (preparedStatement != null) {
                try { preparedStatement.close(); } catch (SQLException e) { IO.println("Failed to close PreparedStatement in getTutorialById: " + e.getMessage()); }
            }
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException e) { IO.println("Failed to close ResultSet in getTutorialById: " + e.getMessage()); }
            }
        }
    }

    //Purpose: Retrieves all tutorials from the database and returns them as a list.
    /* Create list to store all tutorials.
       Run a SELECT * query on the tutorials table.
       Loop through each row, convert to Tutorial using extractTutorialFromResultSet(), and add to the list.
       Return the list.
       Handle errors and close resources with meaningful error messages in catch blocks.
    */
    @Override
    public ArrayList<Tutorial> getAllTutorials() throws DatabaseOperationException {
        ArrayList<Tutorial> tutorials = new ArrayList<>();
        String SQL = "SELECT * FROM tutorials ORDER BY tutorial_id";

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            //Setup SQL and Get Connection
            connection = DBConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL); // The resultSet holds the rows
            //Methods enter while loop, Loop over each row, call extractTutorialFromResultSet() method, where
            // for each row, we create A Tutorial object with data( here id, title, author, url, PublishedDate )
            // add those objects for those rows to the list.
            // Number of loop iterations in while loop = Number of rows in resultSet
            while (resultSet.next()) {
                tutorials.add(extractTutorialFromResultSet(resultSet));
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error retrieving all tutorials: " + e.getMessage(), e);
        } finally {
            //Closes DB resources with safe error messages if something fails.
            DBConnection.closeConnection(connection);
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close Statement in getAllTutorials: " + e.getMessage());
                }
            }
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close ResultSet in getAllTutorials: " + e.getMessage());
                }
            }
        } // Return list with all Tutorial objects.
        return tutorials;
    }

    //Purpose: Updates an existing tutorial in the database by ID.
    /*
   Prepare an SQL UPDATE statement with placeholders for title, author, URL, and published_date.
   Establish a connection to the database.
   Create a PreparedStatement using the SQL string.
   Set values in the PreparedStatement from the Tutorial object.
   Execute the update and check if any rows were affected.
   If no rows were updated, throw TutorialNotFoundException indicating no match for the ID.
   Catch SQL exceptions and wrap them in DatabaseOperationException.
   Finally, close database resources safely with error handling.
   */
    @Override
    public void updateTutorial(Tutorial tutorial) throws TutorialNotFoundException, DatabaseOperationException {
        String SQL = "UPDATE tutorials SET title = ?, author = ?, url = ?, published_date = ? WHERE tutorial_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setString(1, tutorial.getTitle());
            preparedStatement.setString(2, tutorial.getAuthor());
            preparedStatement.setString(3, tutorial.getUrl());
            preparedStatement.setDate(4, tutorial.getPublishedDate() != null
                    ? Date.valueOf(tutorial.getPublishedDate())
                    : null
            );
            preparedStatement.setInt(5, tutorial.getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new TutorialNotFoundException("Tutorial with ID " + tutorial.getId() + " not found for update.");
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error updating tutorial: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close PreparedStatement in updateTutorial: " + e.getMessage());
                }
            }
        }
    }

    //Purpose: Deletes a tutorial by ID from the database.
    /*
   Prepare an SQL DELETE statement with a placeholder for tutorial_id.
   Establish a connection to the database.
   Create a PreparedStatement and set the tutorial ID parameter.
   Execute the delete operation and check how many rows were affected.
   If no rows were deleted, throw TutorialNotFoundException indicating the ID was not found.
   Catch SQL exceptions and rethrow as DatabaseOperationException with a meaningful message.
   In the finally block, safely close all database resources with error logging.
   */

    @Override
    public void deleteTutorial(int id) throws TutorialNotFoundException, DatabaseOperationException {
        String SQL = "DELETE FROM tutorials WHERE tutorial_id = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DBConnection.getConnection();
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new TutorialNotFoundException("Tutorial with ID " + id + " not found for deletion.");
            }

        } catch (SQLException e) {
            throw new DatabaseOperationException("Error deleting tutorial: " + e.getMessage(), e);
        } finally {
            DBConnection.closeConnection(connection);
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close PreparedStatement in deleteTutorial: " + e.getMessage());
                }
            }
        }
    }

}
