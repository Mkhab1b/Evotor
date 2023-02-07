package ru.qualitylab.evotor.evotorMKH;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Position;

public class ItemOrder extends AppCompatActivity {

    MainActivity mainActivity = new MainActivity();

    private TextView contrAgent;
    private TextView debt;
    private TextView number;
    private TextView address;
    private TextView listProduct;
    private String textPay = "";
    JSONObject orderArray = new JSONObject();
    JSONObject loginUser = new JSONObject();

    JSONArray orderItem;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_order);

        init();
        button();
        getData();



        int[] all = new int[]{47500, 12300, 6900, 36900, 22000, 1900};
        int sum = 0;
        int dolg = 65370;
        int allSum = 127500;

        for (int i : all) {
            if (i != all[all.length - 1]) {
                double percent = (i * 100.0 / allSum);
                int price = (int) Math.round(dolg * (percent / 100));
                Log.i("TestMKH", String.valueOf(price));
                sum += price;
            } else {
                int price = dolg - sum;
                Log.i("TestMKH", String.valueOf(price));
                sum += price;
            }
        }
        Log.i("AllDolg", "SUM-2  " + String.valueOf(sum));

    }

    private void init() {
        contrAgent = findViewById(R.id.contrAgent);
        debt = findViewById(R.id.debt);
        number = findViewById(R.id.number);
        address = findViewById(R.id.address);
        listProduct = findViewById(R.id.listProduct);

        String data = getSharedPreferences("App",0).getString("User","0");
        loginUser = null;
        if (data != null) {
            try {
                loginUser = new JSONObject(data);
            } catch (JSONException e) {

            }
        }
    }

    private void button() {
        findViewById(R.id.payButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    payOrder();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getData() {
        // Received data order
        Intent intent = getIntent();
        if (intent.hasExtra("json")) {

            try {
                // Setting the order display values
                orderArray = new JSONObject(intent.getStringExtra("json"));
                contrAgent.setText(orderArray.getString("Контрагент"));
                debt.setText("47500" + " ₽");//orderArray.getString("НомерЗаказа"));
                number.setText(orderArray.getString("Телефон"));
                address.setText(orderArray.getString("АдресПолучателя"));

                // Setting values displaying order lines
                JSONArray itemsOrder = orderArray.getJSONArray("СтрокиЗаказа");
                orderItem = orderArray.getJSONArray("СтрокиЗаказа");
                String allProduct = "";

                for (int i = 0; i < itemsOrder.length(); i++) {
                    JSONObject dataItems = itemsOrder.getJSONObject(i);
                    allProduct += dataItems.getString("Количество") + " x " + dataItems.getString("Номенклатура") + "\n\n";
                }
                listProduct.setText(allProduct);

                // String for display dialog alert
                textPay = "Имя:  " + orderArray.getString("Контрагент") + "\n"
                        + "Телефон:  " + orderArray.getString("Телефон") + "\n"
                        + "Сумма к оплате:  " + "47500" + "₽"; //orderArray.getString("Долг") + "₽";


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    // Function call alert dialog or information that the order paid
    public void payOrder() throws JSONException {

        // Check order paid
        if (orderArray.getBoolean("orderPaid")) {

            Toast.makeText(ItemOrder.this, "Заказ оплачен!", Toast.LENGTH_SHORT).show();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ItemOrder.this);
            builder.setTitle("Оплата заказа")
                    .setMessage(textPay)
                    .setCancelable(false)
                    .setPositiveButton("Оплата", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                openReceipt();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // Function for call the payment window
    public void openReceipt() throws JSONException {
        //Дополнительное поле для позиции. В списке наименований расположено под количеством и выделяется синим цветом

        // Variable for counting debt
        int sumDebtItem = 0;
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Зубочистки"));
        //Создание списка товаров чека
        List<PositionAdd> positionAddList = new ArrayList<>();


        // Function calculate price item for sending to check list
//        for (int i = 0; i < orderItem.length(); i++) {
//
//            // Getting item from list order
//            JSONObject dataItems = orderItem.getJSONObject(i);
//
//            if (i != orderItem.length()-1) {
//
//                // Calculating the percentage of the total debt
//                double percent = (dataItems.getInt("price") * 100.0 / orderArray.getInt("AllSum"));
//                int priceToPay = (int) Math.round(orderArray.getInt("Debt") * (percent / 100));
//                Log.i("TestMKH", String.valueOf(priceToPay));
//                sumDebtItem += priceToPay;
//
//                // Add position to check
//                positionAddList.add(
//                        new PositionAdd(
//                                Position.Builder.newInstance(
//                                                //UUID позиции
//                                                UUID.randomUUID().toString(),
//                                                //UUID товара
//                                                null,
//                                                //Наименование
//                                                dataItems.getString("Номенклатура"),
//                                                //Наименование единицы измерения
//                                                "шт",
//                                                //Точность единицы измерения
//                                                0,
//                                                //Цена без скидок
//                                                new BigDecimal(priceToPay),
//                                                //Количество
//                                                new BigDecimal(dataItems.getString("Количество"))
//                                                //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
//                                        ).build()
//                        )
//                );
//
//            } else {
//
//                // The last item of the price calculating subtraction from all debt
//                int priceToPay = orderArray.getInt("Debt") - sumDebtItem;
//                Log.i("TestMKH", String.valueOf(priceToPay));
//                sumDebtItem += priceToPay;
//
//                positionAddList.add(
//                        new PositionAdd(
//                                Position.Builder.newInstance(
//                                        //UUID позиции
//                                        UUID.randomUUID().toString(),
//                                        //UUID товара
//                                        null,
//                                        //Наименование
//                                        dataItems.getString("Номенклатура"),
//                                        //Наименование единицы измерения
//                                        "шт",
//                                        //Точность единицы измерения
//                                        0,
//                                        //Цена без скидок
//                                        new BigDecimal(priceToPay),
//                                        //Количество
//                                        new BigDecimal(dataItems.getString("Количество"))
//                                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
//                                ).build()
//                        )
//                );
//
//            }
//        }

        for (int i = 0; i < orderItem.length(); i++) {

            JSONObject dataItems = orderItem.getJSONObject(i);

            positionAddList.add(
                    new PositionAdd(
                            Position.Builder.newInstance(
                                            //UUID позиции
                                            UUID.randomUUID().toString(),
                                            //UUID товара
                                            null,
                                            //Наименование
//                                            dataItems.getString("Номенклатура"),
                                            "Пакет",
                                            //Наименование единицы измерения
                                            "шт",
                                            //Точность единицы измерения
                                            0,
                                            //Цена без скидок
//                                            new BigDecimal(dataItems.getLong("price")),
                                            new BigDecimal(1),
                                            //Количество
//                                            new BigDecimal(dataItems.getString("Количество"))
                                            new BigDecimal(1)
                                            //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                                    ).build()
                    )
            );

        }

        Log.i("CHECK", String.valueOf(positionAddList));
        //Дополнительные поля в чеке для использования в приложении
        JSONObject object = new JSONObject();
        try {
            object.put("uidOrder", orderArray.getString("УИД"));
            object.put("loginUser", loginUser.getString("login_UT"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        //Открытие чека продажи. Передаются: список наименований, дополнительные поля для приложения
        new OpenSellReceiptCommand(positionAddList, extra).process(ItemOrder.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(new Intent("evotor.intent.action.payment.SELL"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}