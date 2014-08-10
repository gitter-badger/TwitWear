package jp.tsur.twitwear.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import jp.tsur.twitwear.R;
import jp.tsur.twitwear.service.TwitterService;
import jp.tsur.twitwear.utils.ProgressUtils;


public class MyActivity extends Activity {

    private static final int SPEECH_REQUEST_CODE = 0;

    @InjectView(R.id.list)
    WearableListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        mListView.setAdapter(new Adapter(this));
        mListView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                if (viewHolder.getPosition() == 0) {
                    displaySpeechRecognizer();
                } else {
                    Intent intent = new Intent(MyActivity.this, TwitterService.class);
                    intent.putExtra(TwitterService.EXTRA_ACTION, TwitterService.DATA_MAP_PATH_TIMELINE);
                    startService(intent);
                    ProgressUtils.showProgressDialog(MyActivity.this);
                }
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra(PostActivity.EXTRA_STATUS_TEXT, spokenText);
            startActivity(intent);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final class Adapter extends WearableListView.Adapter {
        private final LayoutInflater mInflater;
        private Context mContext;

        private Adapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mContext = context;
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WearableListView.ViewHolder(
                    mInflater.inflate(R.layout.notif_preset_list_item, null));
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            TextView view = (TextView) holder.itemView.findViewById(R.id.name);
            view.setText(mContext.getResources().getStringArray(R.array.menu)[position]);
            holder.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return mContext.getResources().getStringArray(R.array.menu).length;
        }
    }
}
