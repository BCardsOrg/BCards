//package com.bcards.eu.bcards;
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.TextView;
//
//import com.bcards.eu.common.DataClassifyResult;
//
//import java.util.List;
//
///**
// * Created by Eugen.Horovitz on 21/12/2014.
// */
//public class MyArrayAdapter extends ArrayAdapter<DataClassifyResult> {
//
//    private final Activity context;
//    private final DataClassifyResult list;
//
//    public MyArrayAdapter(Activity context, DataClassifyResult list) {
//        super(context, R.layout.list_item_bcard, list);
//        this.context = context;
//        this.list = list;
//    }
//
//    public View getView(final int position, View convertView, ViewGroup parent) {
//
//        LayoutInflater inflater = context.getLayoutInflater();
//        View rowView = inflater.inflate(R.layout.list_item_bcard, null, true);
//        final TextView text = (TextView) rowView.findViewById(R.id.list_item_bcards_textview);
//        CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.list_item_bcards_checkbox);
//        final Button btnChild = (Button) rowView.findViewById(R.id.list_item_bcards_button);
//        //text.setText(list.get(position).getName());
//        text.setText(list.get(position).getClass().getName());
//
//
//        //checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            //public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//               // // TODO Auto-generated method stub
//               // if (isChecked) {
//                //    text.setTextColor(Color.parseColor("#468765"));
//                 //   btnChild.setText("Revert");
//                 //   DataFieldRecognition fieldItem = list.get(position);
//                 //   list.remove(position);
//                 //   list.add(fieldItem);
//                //}
//            //}
//        //});
//        return rowView;
//    }
//}