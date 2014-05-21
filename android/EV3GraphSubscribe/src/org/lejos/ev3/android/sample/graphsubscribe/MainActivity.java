package org.lejos.ev3.android.sample.graphsubscribe;

import java.io.IOException;
import java.util.Collection;

import lejos.robotics.filter.PublishedSource;
import lejos.robotics.filter.SubscribedProvider;
import lejos.utility.Delay;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity {
  GraphViewSeries rangeSeries;
  GraphView graphView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    rangeSeries = new GraphViewSeries(new GraphViewData[] {});

    graphView = new BarGraphView(this, "Range readings");
    graphView.addSeries(rangeSeries);

    LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
    layout.addView(graphView);

    graphView.setScrollable(true);

    new Control().execute(rangeSeries);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  private class Control extends AsyncTask<GraphViewSeries, Integer, Long> {
    private int numSamples = 0;

    protected Long doInBackground(final GraphViewSeries... series) {
      Collection<PublishedSource> sources = PublishedSource.getSources();

      for (PublishedSource source : sources) {
        if (source.getName().contains("IR")) {
          try {
            SubscribedProvider sp = source.connect();
            final float[] sample = new float[sp.sampleSize()];
            for (numSamples = 0; sp.isActive(); numSamples++) {
              sp.fetchSample(sample, 0);
              runOnUiThread(new Runnable() {
                public void run() {
                  series[0].appendData(new GraphViewData(numSamples + 1,
                      sample[0]), false, 50);
                  graphView.redrawAll();
                }
              });
              Delay.msDelay((long) (1000 / source.getFrequency()));
            }
          } catch (IOException e) {
            // ignore
          }
          try {
            source.close();
          } catch (IOException e) {
          }
          break;
        }
      }
      return (long) numSamples;
    }

    protected void onPostExecute(Long result) {
      Toast.makeText(MainActivity.this, "Number of samples: " + result,
          Toast.LENGTH_LONG).show();
    }
  }

}
