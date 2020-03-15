package com.example.centrrecipt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.TaxNumber;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {


    public static final String LOG_TAG = "com";


    Button openReciptButton;
    String curentUnixTime;

    EditText editTextINN;
    EditText editTextKKT;
    EditText editTextFiscal;

    TextView sizeRecipt;

    Integer reciptInt;


    Boolean cikke;
    Boolean statusCheck;

    Timer timer;
    TimerTask mTimerTask;

    Integer index;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextINN = findViewById(R.id.editTextINN);
        editTextKKT = findViewById(R.id.editTextKKT);
        editTextFiscal = findViewById(R.id.editTextFiscal);

        sizeRecipt = findViewById(R.id.sizeRecipt);

        reciptInt = 0;

        sizeRecipt.setText("Чеков в очереди: " +  reciptInt);


        openReciptButton = findViewById(R.id.openReciptButton);


        openReciptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ItemTaskCentrRecipt().execute();

            }
        });


        timer = new Timer();
        mTimerTask = new MyTimerTask();

        Log.d(LOG_TAG, "время пришло t = " + 60000);
        timer.schedule(mTimerTask, 60000, 60000);

    }



    class MyTimerTask extends TimerTask {
        @Override
        public void run() {


//
            runOnUiThread(new Runnable(){

                // Отображаем информацию в текстовом поле count:
                @Override
                public void run() {

                    Log.d(LOG_TAG, "timer + ");
                    new ItemTaskCentrRecipt().execute();

                }});
        }
    }


    //метод

    public  class ItemTaskCentrRecipt extends AsyncTask<Void, Void, List<ItemCentrRecipt>> {


        @Override
        protected List<ItemCentrRecipt> doInBackground(Void... voids) {

            return new APICentrRreceipt().itemCentrRecipts();

        }

        @SuppressLint("LongLogTag")
        @Override
        protected void onPostExecute(List<ItemCentrRecipt> itemCentrRecipts) {
            super.onPostExecute(itemCentrRecipts);



            index = 0;
            int sizeArray = itemCentrRecipts.size();
            cikke = true;
            statusCheck= true;
            sizeRecipt.setText("Чеков в очереди: " +  sizeArray);

            if (sizeArray <= 0){

                Toast.makeText(MainActivity.this, "Данные на сервере отсутствуют", LENGTH_SHORT).show();

            }else {

                Thread myThread = new Thread(new Runnable() {
                    @Override
                    public void run() {


                        while (cikke) {


                            if (statusCheck == true){


//
                                openReceipt(itemCentrRecipts.get(index).getCoast(),
                                             itemCentrRecipts.get(index).getName(),
                                            0,
                                            itemCentrRecipts.get(index).getType(),
                                            itemCentrRecipts.get(index).getQr_code());

                                statusCheck = false;
                                index++;

                                sizeRecipt.setText("Чеков в очереди: " +  sizeArray);


                                if (index == sizeArray){

                                    cikke = false;
                                    Log.d(LOG_TAG, "false цикл");
                                }

                            }


                        }

                    }
                });
                myThread.start();
            }



        }
    }



    //печать чека


    public void openReceipt(Float decimal, String names, Integer tax, Integer type, String unix) {


        Log.d("Приход ", "цена = " + decimal.toString() + " name =" + names + " tax = " + tax + " type = " + type.toString());

        curentUnixTime = unix;


        PaymentSystem paymentSystem = null;

        if (type == 0) {
            paymentSystem = new PaymentSystem(PaymentType.CASH, "Internet", "12424");
        } else if (type == 1) {
            paymentSystem = new PaymentSystem(PaymentType.ELECTRON, "Internet", "12424");
        }


        BigDecimal bd = BigDecimal.valueOf(decimal);

        // Этот код написал Олежа Ясно солнышко , следующий день после негритянок эхеххех

        Position.Builder pos = Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                null,
                names,
                "1",
                0,
                bd,
                BigDecimal.ONE
        );
        switch (tax) {
            case 0:
                pos.setTaxNumber(TaxNumber.NO_VAT);
                //pos.build();
                break;
            case 1:
                pos.setTaxNumber(TaxNumber.VAT_0);
                //pos.build();
                break;
            case 2:
                pos.setTaxNumber(TaxNumber.VAT_10);
                //pos.build();
                break;
            case 3:
                pos.setTaxNumber(TaxNumber.VAT_10_110);
                //pos.build();
                break;
            case 4:
                pos.setTaxNumber(TaxNumber.VAT_18);
                //pos.build();
                break;
            case 5:
                pos.setTaxNumber(TaxNumber.VAT_18_118);
                //pos.build();
                break;
            default:
                break;
        }


        List<Position> list = new ArrayList<>();
        list.add(pos.build());


        //Position.Builder.newInstance()

        HashMap payments = new HashMap<Payment, BigDecimal>();
        PaymentPerformer paymentPerformer = new PaymentPerformer(paymentSystem, getPackageName()
                , MainActivity.class.getName()
                , "f98ed05f-0d1e-4524-b1b1-4800ce69e998"
                , getApplicationName(MainActivity.this));


        Payment payment = new Payment(
                UUID.randomUUID().toString(),
                bd,
                paymentSystem,
                paymentPerformer,
                "purposeIdentifier",
                "accountId",
                "accountUserDescription"
        );

        payments.put(payment, bd);


        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, null, null, true);
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                printGroup,
                list,
                payments,
                new HashMap<Payment, BigDecimal>(), new HashMap<String, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);

        Receipt slip = ReceiptApi.getReceipt(MainActivity.this, Receipt.Type.SELL);


        JSONObject object = new JSONObject();



        try {
            object.put("qr_code", unix);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SetExtra extra = new SetExtra(object);


        new PrintSellReceiptCommand(listDocs, extra, null, "infosaverapp@yandex.ru", null, null, null).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {


                try {


                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();


                    switch (result.getType()) {
                        case OK:

                            break;
                        case ERROR:


                            switch (result.getError().getCode()) {

                                case PrintReceiptCommandResult.ERROR_CODE_DATETIME_SYNC_REQUIRED:

                                    Toast.makeText(MainActivity.this, "Ошибка: Нужна синхронизация даты/времени ККМ и терминала", Toast.LENGTH_LONG).show();

                                    break;
                                case PrintReceiptCommandResult.ERROR_CODE_SESSION_TIME_EXPIRED:

                                    Toast.makeText(MainActivity.this, "Ошибка: Время сессии превысило 24 часа, закройте смену открыв меню в правом верхнем углу", Toast.LENGTH_LONG).show();

                                    break;

                                case PrintReceiptCommandResult.ERROR_CODE_EMAIL_AND_PHONE_ARE_NULL:

                                    Toast.makeText(MainActivity.this, "Ошибка: В интерет чеках поля 'эл.почта' и/или 'телефон' клиента должны быть заполнены", Toast.LENGTH_LONG).show();

                                    break;

                                case PrintReceiptCommandResult.ERROR_CODE_KKM_IS_BUSY:

                                    Toast.makeText(MainActivity.this, "Ошибка: ККМ в данный момент выполняет другую операцию", Toast.LENGTH_LONG).show();

                                    break;

                                case PrintReceiptCommandResult.ERROR_CODE_NO_AUTHENTICATED_USER:

                                    Toast.makeText(MainActivity.this, "Ошибка: Нет авторизованного пользователя на терминале", Toast.LENGTH_LONG).show();

                                    break;
                                case PrintReceiptCommandResult.ERROR_CODE_PRINT_DOCUMENT_CREATION_FAILED:

                                    Toast.makeText(MainActivity.this, "Ошибка создания документа для печати", Toast.LENGTH_LONG).show();

                                    break;
                                case PrintReceiptCommandResult.ERROR_CODE_NO_PERMISSION:

                                    Toast.makeText(MainActivity.this, "Ошибка: У приложения нет необходимого разрешения (permission)", Toast.LENGTH_LONG).show();

                                    break;
                                case PrintReceiptCommandResult.ERROR_CODE_NO_POSITIONS:

                                    Toast.makeText(MainActivity.this, "Ошибка: Нет позиций в чеке", Toast.LENGTH_LONG).show();

                                    break;

                                case PrintReceiptCommandResult.ERROR_CODE_NO_PAYMENTS:

                                    Toast.makeText(MainActivity.this, "Ошибка: Нет позиций в чеке", Toast.LENGTH_LONG).show();

                                    break;

                                case PrintReceiptCommandResult.ERROR_KKM_IS_NOT_AVAILABLE:

                                    Toast.makeText(MainActivity.this, "Ккм не доступна", Toast.LENGTH_LONG).show();

                                    break;


                                default:
                                    break;
                            }


                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });

    }






    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }




    public String getUnixTime() {

        long unixTime = System.currentTimeMillis() / 1000L;
        String strLong = Long.toString(unixTime);

        return strLong;

    }
}
