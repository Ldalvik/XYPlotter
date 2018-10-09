import android.content.Context;
import android.widget.Toast;

public class Utils {
    private Context context;

    public Utils(Context context){
        this.context = context;
    }

    public void makeToast(String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
