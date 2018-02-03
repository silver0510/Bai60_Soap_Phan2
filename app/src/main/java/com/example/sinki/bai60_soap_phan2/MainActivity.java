package com.example.sinki.bai60_soap_phan2;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sinki.config.Configuration;
import com.example.sinki.model.Contact;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {
    EditText txtGetMa;
    TextView txtMa,txtTen,txtPhone;
    Button  btnGet;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControl();
        addEvents();
    }

    private void addEvents() {
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLayThongTin();
            }
        });
    }

    private void xuLyLayThongTin() {
        int ma = Integer.parseInt(txtGetMa.getText().toString());
        ContactTask task = new ContactTask();
        task.execute(ma);
    }

    private void addControl() {
        txtGetMa = (EditText) findViewById(R.id.txtGetMa);
        txtMa = (TextView) findViewById(R.id.txtMa);
        txtTen = (TextView) findViewById(R.id.txtTen);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        btnGet = (Button) findViewById(R.id.btnGet);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Thông báo");
        dialog.setMessage("Đang lấy thông tin...");
        dialog.setCanceledOnTouchOutside(false);
    }
    private class ContactTask extends AsyncTask<Integer,Void,Contact>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txtMa.setText("");
            txtTen.setText("");
            txtPhone.setText("");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Contact contact) {
            super.onPostExecute(contact);
            dialog.dismiss();
            txtMa.setText(contact.getMa()+"");
            txtTen.setText(contact.getTen());
            txtPhone.setText(contact.getPhone());
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Contact doInBackground(Integer... params) {
            try
            {
                int ma = params[0];
                SoapObject request = new SoapObject(Configuration.NAME_SPACE,Configuration.METHOD_GETDETAIL);
                request.addProperty(Configuration.PARAM_DETAIL_MA,ma);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(Configuration.SERVER_URL);
                httpTransportSE.call(Configuration.SOAP_ACTION_GETDETAIL,envelope);

                SoapObject data = (SoapObject) envelope.getResponse();
                Contact contact = new Contact();
                if(data.hasProperty("Ma"))
                    contact.setMa(Integer.parseInt(data.getPropertyAsString("Ma")));
                if(data.hasProperty("Ten"))
                    contact.setTen(data.getPropertyAsString("Ten"));
                if(data.hasProperty("Phone"))
                    contact.setPhone(data.getPropertyAsString("Phone"));
                return contact;
            }
            catch (Exception ex)
            {
                Log.e("LOI",ex.toString());
            }
            return null;
        }
    }
}
