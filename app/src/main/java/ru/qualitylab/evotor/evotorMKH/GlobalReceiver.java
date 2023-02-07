package ru.qualitylab.evotor.evotorMKH;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import ru.evotor.framework.core.action.event.cash_drawer.CashDrawerOpenEvent;
import ru.evotor.framework.core.action.event.cash_operations.CashInEvent;
import ru.evotor.framework.core.action.event.cash_operations.CashOutEvent;
import ru.evotor.framework.core.action.event.inventory.ProductCardOpenedEvent;
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionAddedEvent;
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionEditedEvent;
import ru.evotor.framework.core.action.event.receipt.position_edited.PositionRemovedEvent;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClearedEvent;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptClosedEvent;
import ru.evotor.framework.core.action.event.receipt.receipt_edited.ReceiptOpenedEvent;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;

/**
 * Получение событий об открытии чека, обновлении базы продуктов или результате изменения чека
 * Смарт терминал не ждёт ответ от приложения на широковещательные сообщения.
 */
public class GlobalReceiver extends BroadcastReceiver {

    private static String uuidOrder = "MKH";

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        Log.e(getClass().getSimpleName(), action);
        Receipt info = ReceiptApi.getReceipt(context, Receipt.Type.SELL);

        if (action != null) {
            switch (action) {
                //Чек продажи был успешно открыт
                case "evotor.intent.action.receipt.sell.OPENED":

//                    Toast.makeText(context, "\nUUID:" + ReceiptOpenedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
//                    String uidOrder = "";
//                    String loginUser = "";
//                    try {
//                        JSONObject order = new JSONObject(info.getHeader().component7());
//                        uidOrder = order.getString("uidOrder");
//                        loginUser = order.getString("loginUser");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Toast.makeText(context, uidOrder + "\n" + loginUser, Toast.LENGTH_LONG).show();

//                    if (uidOrder.length() > 0 && loginUser.length() > 0) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    Thread.sleep(5000);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                Intent intent = new Intent (context, ItemOrder.class);
////                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                context.startActivity(intent);
//                            }
//                        }).start();


//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                URL generateUrl = Request.getTestUrl();
//                                String response;
//                                try {
//                                    response = Request.getRequest(generateUrl);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//
//                                Intent intent = new Intent (context, ItemOrder.class);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                context.startActivity(intent);
//                            }
//                        }).start();
//                    }


                    break;

                //Чек продажи был успешно закрыт
                case "evotor.intent.action.receipt.sell.RECEIPT_CLOSED":

                    Log.e(getClass().getSimpleName(), "Data:" + ReceiptClosedEvent.create(bundle).getReceiptUuid());

//                    Toast.makeText(context,"Receipt UUID -  " + ReceiptClosedEvent.create(bundle).getReceiptUuid(), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(context, "UUID PROGRAM - " + uuidOrder, Toast.LENGTH_SHORT).show();


                    String uidOrder = "";
                    String loginUser = "";
                    try {
                        JSONObject order = new JSONObject(info.getHeader().component7());
                        uidOrder = order.getString("uidOrder");
                        loginUser = order.getString("loginUser");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    
                    if ( uidOrder.length() > 0 && loginUser.length() > 0 ) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                URL generateUrl = Request.getTestUrl();
                                String response = null;
                                try {
                                    response = Request.getRequest(generateUrl);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
//                                try {
//                                    Thread.sleep(2500);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                Toast.makeText(context, "Helllo", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent (context, MainActivity.class);
                                intent.putExtra("test",response);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);
                            }
                        }).start();
                    }

                    break;
            }
        }
    }

}
