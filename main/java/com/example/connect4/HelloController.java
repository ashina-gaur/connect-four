package com.example.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HelloController implements Initializable {
  private static final int COLUMNS=7;
  private static final int ROWS=6;
  private static final int CIRCLE_DIAMETER=80;
  private static final String discColor1="#24303E";
  private static final String discColor2="#4CAA88";
  private static String PLAYER_ONE="Player One";
  private  static String PLAYER_TWO="Player Two";

  private boolean isPlayerOneTurn=true;
  private Disc[][] insertedDiscsArray=new Disc[ROWS][COLUMNS];//for structural changes..for developers

  @FXML
  public GridPane rootGridPane;
  @FXML
  public Pane insertedDiscsPane;
  @FXML
  public Label playerNameLabel;
  @FXML
  public TextField playerOneTextField,playerTwoTextField;
  @FXML
  public Button setNamesButton;
  private boolean isAllowedToInsert=true;
  public void createPlayground(){
    Shape rectagleWithHoles=createGameStructuralGrid();
    rootGridPane.add(rectagleWithHoles,0,1);
    List<Rectangle> rectangleList=createClickableColumns();
    for(Rectangle rectangle:rectangleList) {
      rootGridPane.add(rectangle, 0, 1);
    }
    setNamesButton.setOnAction(ActionEvent->
    {
        PLAYER_ONE=playerOneTextField.getText();
        PLAYER_TWO=playerTwoTextField.getText();
    });
  }
  private Shape createGameStructuralGrid(){
    Shape rectagleWithHoles=new Rectangle((COLUMNS+1)*CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);

    for(int row=0;row<ROWS;row++)
    {
      for(int col=0;col<COLUMNS;col++)
      {
        Circle circle=new Circle();
        circle.setRadius(CIRCLE_DIAMETER/2);
        circle.setCenterX(CIRCLE_DIAMETER/2);
        circle.setCenterY(CIRCLE_DIAMETER/2);
        circle.setSmooth(true);
        circle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        circle.setTranslateY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
        rectagleWithHoles=Shape.subtract(rectagleWithHoles,circle);
      }
    }
//we use the translate animation so that with each iteration we are going to subtract our circle from the subsequent position
    rectagleWithHoles.setFill(Color.WHITE);
    return  rectagleWithHoles;
  }
  private List<Rectangle> createClickableColumns()
  {
    List<Rectangle> rectangleList=new ArrayList();
    for(int col=0;col<COLUMNS;col++)
    {
      Rectangle rectangle=new Rectangle(CIRCLE_DIAMETER,(ROWS+1)*CIRCLE_DIAMETER);
      rectangle.setFill(Color.TRANSPARENT);

      rectangle.setTranslateX(col*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
              rectangle.setOnMouseEntered(ActionEvent->rectangle.setFill(Color.valueOf("#eeeeee26")));//if we hover over the rectangle it would change the colour to eeeeee26
      rectangle.setOnMouseExited(ActionEvent->rectangle.setFill(Color.TRANSPARENT));//if we no longer hover it would show it as transparent
      final int column=col;//variable used in lambda expression should be final or effectively final
      rectangle.setOnMouseClicked(ActionEvent->//enabling click event on the rectangles
      {
        if(isAllowedToInsert) {
          isAllowedToInsert=false;//when disc is dropped by one player,no more disc can be dropped by the same player
          insertDisc(new Disc(isPlayerOneTurn), column);//on click of each of these columns we are basically inserting discs,creating a class disc and passing column number
        }});
      rectangleList.add(rectangle);
    }

    return rectangleList;
  }
  //INSERTDISC METHOD
  private  void insertDisc(Disc disc,int column)//two parameters,disc class object and the integer column
  {
    //determining the position of our new disc
    int row=ROWS-1;
    while(row>=0)
    {
      if(getDiscIfPresent(row,column)==null)
      {
        break;//if the block is null we have gotten an empty space
      }
      row--;//if array is not empty then decrement the row value
    }
    if(row<0)//if row is full we cannot insert any more disc
      return;
    //the purpose of the while loop written above is to get the row which is empty so that in the next statement written below,we can insert the disc into that place
    insertedDiscsArray[row][column]=disc;//for structural changes
    insertedDiscsPane.getChildren().add(disc);//inserted discs pane is the  pane we had defined in the scene builder in which we are adding our disc visually
    disc.setTranslateX(column*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
    int currentRow=row;
    TranslateTransition translateTransition=new TranslateTransition(Duration.seconds(0.5),disc);//we pass the disc as parameter as the transition is applied on the disc

    translateTransition.setToY(row*(CIRCLE_DIAMETER+5)+CIRCLE_DIAMETER/4);
    //after a disc has been inserted we need to toggle between the players
    //for that we use setOnFinish and declare isplayeroneturn as false
    translateTransition.setOnFinished(ActionEvent->{
      isAllowedToInsert=true;//when disc is dropped to the bottom,we can now allow next player to insert the disc
      if(gameEnded(currentRow,column))
      {
        gameOver();
        return;
      }
      isPlayerOneTurn=!isPlayerOneTurn;
      playerNameLabel.setText(isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO);//setting text for player name label so as to display to the user who's turn it is
    });
    translateTransition.play();//to play our animation
  }
  //GAME ENDED METHOD
  private boolean gameEnded(int row,int column)//must return a boolean value
  {
    //vertical combinations
    //column value remains constant,row value changes
    //range of row values will be =0,1,2,3,4,5
    //index of each element present in column[row][column]:0,3 1,3 2,3 3,3 4,3 5,3
    //we need to find a class that holds all these pair of values-> in java we have a class called point2D class which holds value in terms of x and y coordinates
    List<Point2D> verticalPoints=IntStream.rangeClosed(row-3,row+3)//range of row values=0,1,2,3,4,5
            .mapToObj(r->new Point2D(r,column))//0,3 1,3 2,3 3,3 4,3 5,3
            .collect(Collectors.toList());//using collect method to transform them into point2D objects
    //for horizontal row will remain constant column will change
    List<Point2D> horizontalPoints=IntStream.rangeClosed(column-3,column+3)//range of column values=0,1,2,3,4,5
            .mapToObj(c->new Point2D(row,c))//0,3 1,3 2,3 3,3 4,3 5,3
            .collect(Collectors.toList());
    Point2D startPoint1=new Point2D(row-3,column+3);
    List<Point2D> diagonal1Points=IntStream.rangeClosed(0,6)
            .mapToObj(i->startPoint1.add(i,-i))
            .collect(Collectors.toList());
    Point2D startPoint2=new Point2D(row-3,column-3);
    List<Point2D> diagonal2Points=IntStream.rangeClosed(0,6)
            .mapToObj(i->startPoint2.add(i,i))
            .collect(Collectors.toList());
    boolean isEnded=checkCombinations(verticalPoints)||checkCombinations(horizontalPoints)
            ||checkCombinations(diagonal1Points)||checkCombinations(diagonal2Points);
    //isEnded=checkCombinations(horizontalPoints);
    return isEnded;
  }

  private boolean checkCombinations(List<Point2D> points) {
    int chain=0;
    for(Point2D point:points)
    {

      int rowIndexForArray=(int)point.getX();
      int columnIndexForArray=(int)point.getY();
      //checking if disc is present or not
      Disc disc =getDiscIfPresent(rowIndexForArray,columnIndexForArray);
      if(disc!=null && disc.isPlayerOneMove==isPlayerOneTurn) {
        chain++;
        if (chain == 4) {
          return true;
        }
      }else {
          chain = 0;
        }

    }
    return false;
  }
private Disc getDiscIfPresent(int row,int column)
{
  if(row>=ROWS||row<0||column>=COLUMNS||column<0)
    return null;

   return insertedDiscsArray[row][column];
}
  private void gameOver()
  {
    Alert alert=new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Connect 4");


   String winner=isPlayerOneTurn?PLAYER_ONE:PLAYER_TWO;
    alert.setHeaderText("The Winner is"+winner);
    alert.setContentText("Want to play again?");
    ButtonType yesBtn=new ButtonType("yes");
    ButtonType noBtn=new ButtonType("No,Exit");
    alert.getButtonTypes().setAll(yesBtn,noBtn);


    Platform.runLater(()->//this function helps us resolve all illegalStateException
    {
      Optional<ButtonType> btnClicked=alert.showAndWait();//returns which button is actually clicked
      if(btnClicked.isPresent()&&btnClicked.get()==yesBtn)//btnclicked.ispresent confirms the existence of a button in the optional class
      {
        resetGame();
      }
      else//user has chosen np
      {
        Platform.exit();
        System.exit(0);
      }
    });
    //optional basically stores the button type object,ideally it can store any type of object



    System.out.println("winner is : "+winner);
  }

  public void resetGame() {
    insertedDiscsPane.getChildren().clear();//removes all inserted discs from the pane//visulal elimination
    //structually
    for(int row=0;row<insertedDiscsArray.length;row++)
    {
      for(int col=0;col<insertedDiscsArray[row].length;col++)
      {
        insertedDiscsArray[row][col]=null;
      }
    }
    isPlayerOneTurn=true;//Let Player 1 start the game
    playerNameLabel.setText(PLAYER_ONE);
    createPlayground();//prepare a fresh playground

  }

  //this class is to help determine the colour of the disk
  private static class Disc extends Circle{//extends from circle cause disc are circular in nature
    private final boolean isPlayerOneMove;
    public Disc(boolean isPlayerOneMove)//if player one inserts the disc
    {
      this.isPlayerOneMove=isPlayerOneMove;
      setRadius(CIRCLE_DIAMETER/2);
      setFill(isPlayerOneMove?Color.valueOf(discColor1):Color.valueOf(discColor2));
      setCenterX(CIRCLE_DIAMETER/2);
      setCenterY(CIRCLE_DIAMETER/2);
    }
  }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}