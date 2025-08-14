package crm.ui;

import crm.dao.CustomerDao;
import crm.dao.InteractionDao;
import crm.model.Customer;
import crm.model.Interaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.Parent;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MainView {
  private final BorderPane root = new BorderPane();

  private final CustomerDao customerDao = new CustomerDao();
  private final InteractionDao interactionDao = new InteractionDao();

  private final ObservableList<Customer> customers = FXCollections.observableArrayList();
  private final ObservableList<Interaction> interactions = FXCollections.observableArrayList();

  private final TableView<Customer> table = new TableView<>();
  private final ListView<String> interactionList = new ListView<>();

  public MainView() {
    root.setTop(buildTopBar());
    root.setCenter(buildCenter());
    root.setRight(buildRightPanel());
    BorderPane.setMargin(root.getRight(), new Insets(10));

    refreshCustomers();
  }

  public Parent getRoot() { return root; }

  // ===== UI builders =====
  private Node buildTopBar() {
    HBox bar = new HBox(8);
    bar.setPadding(new Insets(10));
    bar.setStyle("-fx-background-color: linear-gradient(to right, #2c3e50, #4ca1af);");

    ComboBox<String> field = new ComboBox<>(FXCollections.observableArrayList("Name", "Company", "Industry"));
    field.getSelectionModel().select("Name");
    TextField q = new TextField();
    q.setPromptText("Search...");

    Button btnSearch = new Button("Search");
    Button btnClear  = new Button("Clear");
    Button btnAdd    = new Button("New Customer");
    Button btnDelete = new Button("Delete");

    btnSearch.setOnAction(e -> customers.setAll(customerDao.search(field.getValue(), q.getText().trim())));
    btnClear.setOnAction(e -> { q.clear(); refreshCustomers(); });
    btnAdd.setOnAction(e -> showCustomerDialog(null));
    btnDelete.setOnAction(e -> {
      Customer sel = table.getSelectionModel().getSelectedItem();
      if (sel != null && confirm("Delete " + sel.getName() + "?")) {
        customerDao.delete(sel.getId());
        refreshCustomers();
        interactions.clear();
      }
    });

    bar.getChildren().addAll(field, q, btnSearch, btnClear, new Separator(), btnAdd, btnDelete);
    return bar;
  }

  private Node buildCenter() {
    // table columns
    TableColumn<Customer,String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameCol.setPrefWidth(200);

    TableColumn<Customer,String> compCol = new TableColumn<>("Company");
    compCol.setCellValueFactory(new PropertyValueFactory<>("company"));
    compCol.setPrefWidth(160);

    TableColumn<Customer,String> indCol = new TableColumn<>("Industry");
    indCol.setCellValueFactory(new PropertyValueFactory<>("industry"));
    indCol.setPrefWidth(140);

    TableColumn<Customer,String> emailCol = new TableColumn<>("Email");
    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    emailCol.setPrefWidth(200);

    TableColumn<Customer,String> phoneCol = new TableColumn<>("Phone");
    phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
    phoneCol.setPrefWidth(120);

    TableColumn<Customer, LocalDate> fuCol = new TableColumn<>("Follow-up");
    fuCol.setCellValueFactory(new PropertyValueFactory<>("nextFollowUp"));
    fuCol.setPrefWidth(110);

    table.getColumns().addAll(nameCol, compCol, indCol, emailCol, phoneCol, fuCol);
    table.setItems(customers);

    // row highlighting if follow-up due within 7 days (or overdue)
    table.setRowFactory(tv -> new TableRow<>() {
      @Override protected void updateItem(Customer item, boolean empty) {
        super.updateItem(item, empty);
        setStyle("");
        if (!empty && item != null && item.getNextFollowUp() != null) {
          long days = ChronoUnit.DAYS.between(LocalDate.now(), item.getNextFollowUp());
          if (days < 0) setStyle("-fx-background-color: rgba(255,0,0,0.18);");          // overdue
          else if (days <= 7) setStyle("-fx-background-color: rgba(255,165,0,0.18);"); // due soon
        }
      }
    });

    table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
      if (n != null) refreshInteractions(n.getId());
    });

    VBox box = new VBox(table);
    VBox.setVgrow(table, Priority.ALWAYS);
    box.setPadding(new Insets(10));
    return box;
  }

  private Node buildRightPanel() {
    var pane = new VBox(8);
    pane.setPadding(new Insets(10));
    pane.setPrefWidth(320);
    pane.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd;");

    Label title = new Label("Interactions");
    title.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

    interactionList.setItems(FXCollections.observableArrayList());

    Button addCall    = new Button("Log Call");
    Button addEmail   = new Button("Log Email");
    Button addMeeting = new Button("Log Meeting");
    Button editCust   = new Button("Edit Customer…");

    addCall.setOnAction(e -> log("Call"));
    addEmail.setOnAction(e -> log("Email"));
    addMeeting.setOnAction(e -> log("Meeting"));
    editCust.setOnAction(e -> {
      Customer sel = table.getSelectionModel().getSelectedItem();
      if (sel != null) showCustomerDialog(sel);
    });

    pane.getChildren().addAll(title, interactionList, addCall, addEmail, addMeeting, new Separator(), editCust);
    VBox.setVgrow(interactionList, Priority.ALWAYS);
    return pane;
  }

  // ===== actions =====
  private void refreshCustomers() {
    List<Customer> all = customerDao.listAll();
    customers.setAll(all);
    if (!all.isEmpty()) table.getSelectionModel().selectFirst();
  }

  private void refreshInteractions(int customerId) {
    interactions.setAll(interactionDao.forCustomer(customerId));
    interactionList.getItems().setAll(
      interactions.stream().map(i -> String.format("[%s] %s — %s",
        i.getWhenUtc().toString(), i.getKind(), safe(i.getNote()))).toList()
    );
  }

  private void log(String kind) {
    Customer sel = table.getSelectionModel().getSelectedItem();
    if (sel == null) return;

    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Log " + kind);
    dialog.setHeaderText("Add a note for the " + kind.toLowerCase() + ":");
    dialog.setContentText("Note:");
    var res = dialog.showAndWait();
    if (res.isEmpty()) return;

    var ix = new Interaction(null, sel.getId(), Instant.now(), kind, res.get());
    interactionDao.insert(ix);
    refreshInteractions(sel.getId());
  }

  private void showCustomerDialog(Customer existing) {
    Dialog<Customer> d = new Dialog<>();
    d.setTitle(existing == null ? "New Customer" : "Edit Customer");

    TextField name = new TextField();
    TextField company = new TextField();
    TextField industry = new TextField();
    TextField email = new TextField();
    TextField phone = new TextField();
    DatePicker follow = new DatePicker();

    if (existing != null) {
      name.setText(existing.getName());
      company.setText(existing.getCompany());
      industry.setText(existing.getIndustry());
      email.setText(existing.getEmail());
      phone.setText(existing.getPhone());
      follow.setValue(existing.getNextFollowUp());
    }

    GridPane g = new GridPane();
    g.setHgap(8); g.setVgap(8); g.setPadding(new Insets(10));
    g.addRow(0, new Label("Name"), name);
    g.addRow(1, new Label("Company"), company);
    g.addRow(2, new Label("Industry"), industry);
    g.addRow(3, new Label("Email"), email);
    g.addRow(4, new Label("Phone"), phone);
    g.addRow(5, new Label("Next Follow-up"), follow);

    d.getDialogPane().setContent(g);
    d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    d.setResultConverter(bt -> {
      if (bt == ButtonType.OK) {
        Customer c = existing == null ? new Customer() : existing;
        c.setName(name.getText().trim());
        c.setCompany(company.getText().trim());
        c.setIndustry(industry.getText().trim());
        c.setEmail(email.getText().trim());
        c.setPhone(phone.getText().trim());
        c.setNextFollowUp(follow.getValue());
        return c;
      }
      return null;
    });

    var res = d.showAndWait();
    if (res.isPresent()) {
      var cu = res.get();
      if (cu.getId() == null) customerDao.insert(cu);
      else customerDao.update(cu);
      refreshCustomers();
      if (cu.getId() != null) {
        // keep selection on updated/inserted customer
        customers.stream().filter(x -> x.getId().equals(cu.getId()))
          .findFirst().ifPresent(x -> table.getSelectionModel().select(x));
      }
    }
  }

  private boolean confirm(String msg) {
    return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
      .showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
  }

  private static String safe(String s) { return s == null ? "" : s; }
}
