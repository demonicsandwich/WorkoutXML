/**
* WorkoutXML is a command line interface that allows users to interact with a workout regimen saved on an XML file.
*
* @author A Selim
* @since 12/12/2022
*/

import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import nu.xom.*;
import java.util.ArrayList;

class WorkoutXML {
  private static Scanner kb = new Scanner(System.in);
  private static Element regimen, exercise, name, bodyPart, sets, reps, weight;
  private static Document doc;
  private static String filename = "Workout.xml";

  /**
  * Parses the root element "regimen" from the XML file or creates a blank root element if no XML file exists.
  */
  private static void getRootFile() 
  {
    try
    {
      // Try to build node tree from existing XML to parse the root element.
      File file = new File(filename);
      doc = new Builder().build(file);
      regimen = doc.getRootElement();
    }
    catch (ParsingException e)
    {
      // If no file exists, create a new root element and document object.
      regimen = new Element("regimen");
      doc = new Document(regimen);
    }
    catch (IOException e)
    {
      regimen = new Element("regimen");
      doc = new Document(regimen);
    }
  }

  /**
  * Initializes all elements necessary for a single entry, and defines their hierarchy.
  */
  private static void initializeEntry()
  {
    exercise = new Element("exercise");
    name = new Element("name");
    bodyPart = new Element("bodyPart");
    sets = new Element("sets");
    reps = new Element("reps");
    weight = new Element("weight");

    // Initialize each elements' display names in the program.
    name.addAttribute(new Attribute("displayName", "Name"));
    bodyPart.addAttribute(new Attribute("displayName", "Muscle group(s)"));
    sets.addAttribute(new Attribute("displayName", "# of sets"));
    reps.addAttribute(new Attribute("displayName", "# of reps"));
    weight.addAttribute(new Attribute("displayName", "Weight (lbs)"));

    // Append the exercise to regimen and all of the relevant info to the exercise.
    regimen.appendChild(exercise);
    exercise.appendChild(name);
    exercise.appendChild(bodyPart);
    exercise.appendChild(sets);
    exercise.appendChild(reps);
    exercise.appendChild(weight);
  }

  /**
  * Sets the values of each parameter of a single exercise to the user's given input.
  */
  private static void setElements()
  {
    initializeEntry();
    
    kb.nextLine();
    System.out.println("Name of exercise: ");
    name.appendChild(kb.nextLine());
    System.out.println("Muscle group(s) worked: ");
    bodyPart.appendChild(kb.nextLine());
    System.out.println("Number of sets: ");
    sets.appendChild(kb.nextLine());
    System.out.println("Number of reps: ");
    reps.appendChild(kb.nextLine());
    System.out.println("Weight in lbs (N/A if body weight exercise): ");
    weight.appendChild(kb.nextLine());
  }

  /**
  * Writes modified or newly created workout regimen to an XML file.
  */
  private static void writeToFile()
  {
    try
    {
      FileWriter fw = new FileWriter(filename);
      BufferedWriter writer = new BufferedWriter(fw);
    
      writer.write(doc.toXML());
      writer.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  /**
  * Prints info on each exercise in the regimen.
  */
  private static void printContent()
  {
    if (regimen.getChildCount() > 0)
      System.out.println("Here is the current regimen:\n");
    else
      // If the regimen is empty, inform the user.
      System.out.println("The regimen is currently empty.");
    
    for (Element exercises : regimen.getChildElements())
    {
      // Print all info of each exercise.
      System.out.println();
      printExerciseInfo(exercises);
    }
  }

  /**
  * Prints each stored piece of information on a given exercise.
  *
  * @param currentExercise - The exercise element to print the properties of.
  */
  private static void printExerciseInfo(Element currentExercise)
  {
      for (Element infoPoint : currentExercise.getChildElements())
      {
        // Print each property of the exercise and its display name.
        System.out.println(infoPoint.getAttribute(0).getValue() + ": " + infoPoint.getValue());
      }
  }

  /**
  * Searches through the saved regimen to find and return all exercises that's name or targeted body part contain a user specified string.
  *
  * @param searchTerm - A String term to search the regimen for.
  */
  private static void search(String searchTerm)
  {
    ArrayList<Integer> results = new ArrayList<Integer>();
    Elements exercises = regimen.getChildElements();
    for (int i = 0; i < exercises.size(); i++)
    {
      // Iterate through each saved exercise.
      for (Element info : exercises.get(i).getChildElements())
      {
        // If an exercise contains searchTerm in any of its child elements, save its index.
        if (info.getValue().toLowerCase().contains(searchTerm))
        {
          results.add(i);
          break;
        }
      }
    }

    if (results.size() > 0)
      // Print out how many matches were found.
      System.out.println("Found " + results.size() + " result" + ((results.size() == 1) ? "" : "s") + " for \"" + searchTerm + "\"");
    else
      System.out.println("No matches found.");

    for (int i : results)
    {
      // Print info about each exercise found.
      System.out.println();
      printExerciseInfo(exercises.get(i));
    }
    System.out.println();
  }


  /**
  * Prints options in a menu format for the user to choose and returns their choice.
  *
  * @return int - The number ID of the chosen action option.
  */
  private static int makeChoice()
  {
    clearScreen();
    System.out.println("Welcome to your Workout Regimen! \n\nUsing this workout program, you can add to, search from and print out your personal regimen. Enter the number next to an option and press ENTER to access such method. \n\n1 Add an exercise \n2 Print regimen \n3 Search exercises \n4 Exit \n");
    
    try
    {
      return kb.nextInt();
    }
    catch (Exception e)
    {
      // If user input isn't a number, default to 0 which will be invalid.
      return 0;
    }
  }

  /**
  * Clears the console.
  */
  private static void clearScreen()
  {
    // Make use of ANSI escape sequences to clear the console of any text.
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  /**
  * Acts as a buffer screen and waits for user input before exiting.
  */
  private static void returnToMenu()
  {
    System.out.println("\nPress ENTER to return to the menu");
    kb.nextLine();
  }

  /**
  * Main loop display menu before and after each method run, until either the program is exited or forcibly stopped.
  */
  private static void menuLoop()
  {
    while (true)
    {
      switch(makeChoice())
      {
        case 1:
          // Set exercise parameters to user input and add the exercise to the XML.
          clearScreen();
          System.out.println("This method adds an exercise completely customized by you to your regimen. Please enter in the relevant information regarding the exercise as prompted.\n");
          setElements();
          writeToFile();
          System.out.println("\nSuccessfully added exercise to regimen!");
          returnToMenu();
          break;
          
        case 2:
          // Print all of the exercises from the XML file.
          clearScreen();
          printContent();
          kb.nextLine();
          returnToMenu();
          break;
          
        case 3:
          // Prompt user input and search the XML for all matches.
          clearScreen();
          System.out.println("This method searches your entire regimen for any mention of the search term you provide. All potential matches are then printed on screen. \n\nPlease enter search term: ");
          kb.nextLine();
          search(kb.nextLine().trim().toLowerCase());
          returnToMenu();
          break;
          
        case 4:
          // Exits the method and hence the program.
          clearScreen();
          System.out.println("Goodbye!");
          return;
          
        default:
          // If the user gave an invalid command, let the user know.
          clearScreen();
          System.out.println("Invalid command.");
          kb.nextLine();
          returnToMenu();
      }
    }
  }

  /**
  * Main method loads the XML file and starts the menu loop.
  *
  * @param args - Command line arguments.
  */
  public static void main(String[] args) 
  {
    // Set text colour to green.
    System.out.println("\033[1;32m");
    
    getRootFile();
    menuLoop();
  }
}