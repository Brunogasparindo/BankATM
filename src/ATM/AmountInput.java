package ATM;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AmountInput {
    /*
    * Buttons from the ATM machine
    * */
    @FXML
    private Button one;
    @FXML
    private Button two;
    @FXML
    private Button three;
    @FXML
    private Button four;
    @FXML
    private Button five;
    @FXML
    private Button six;
    @FXML
    private Button seven;
    @FXML
    private Button eight;
    @FXML
    private Button nine;
    @FXML
    private Button zero;

    /*
    * Temporary TextField to help the user to identify the wished amount for the operation
    * */
    @FXML
    private TextField amountField;

    /*
    * TextField that shows the amount after the user presses the "confirm" button
    * This amount will be used later on
    * */
    @FXML
    private TextField confirmedAmountField;

    /*
    * Temporary String used to build the amount
    * */
    private String temporaryNumber = "";

    /*
    * Double amount that will be used in the operation
    * */
    private double confirmedAmount = 0;

    /*
    * Button number clicked
    * */
    @FXML
    public void onNumberBtnClicked (MouseEvent event) {
        int btnClicked = 10;
        if (event.getSource().equals(one)) {
            btnClicked = 1;
        }
        if (event.getSource().equals(two)) {
            btnClicked = 2;
        }
        if (event.getSource().equals(three)) {
            btnClicked = 3;
        }
        if (event.getSource().equals(four)) {
            btnClicked = 4;
        }
        if (event.getSource().equals(five)) {
            btnClicked = 5;
        }
        if (event.getSource().equals(six)) {
            btnClicked = 6;
        }
        if (event.getSource().equals(seven)) {
            btnClicked = 7;
        }
        if (event.getSource().equals(eight)) {
            btnClicked = 8;
        }
        if (event.getSource().equals(nine)) {
            btnClicked = 9;
        }
        if (event.getSource().equals(zero)) {
            btnClicked = 0;
        }

        if (btnClicked >= 10) {
            System.out.println("Error");
            return;
        }

        temporaryNumber = temporaryNumber + btnClicked;
        setAmountField();
    }

    /*
    * Separator Button clicked
    * */
    @FXML
    public void onSeparatorClicked () {
        if (!temporaryNumber.contains(".") && Double.parseDouble(temporaryNumber) != 0) {
            temporaryNumber = temporaryNumber + ".";
        }
    }

    /*
    * Clear button clicked
    * */
    @FXML
    public void onClearResultBtnClick() {
        temporaryNumber = "";
        amountField.setText("0.00");
        confirmedAmountField.setText("0.00");
        confirmedAmount = 0;
    }

    /*
    * Confirm button clicked
    * */
    @FXML
    public void onConfirmBtnClicked()
    {
        confirmedAmount = Double.parseDouble(temporaryNumber);
        confirmedAmountField.setText(temporaryNumber);
    }

    /*
    * Set the amount text filed
    * */
    private void setAmountField()
    {
        amountField.setText(temporaryNumber);
    }

    /*
    * Amount input Dialog Pane process conclusion
    * @return   amount confirmed by the user
    * */
    public double processResult()
    {
        return confirmedAmount;
    }
}
