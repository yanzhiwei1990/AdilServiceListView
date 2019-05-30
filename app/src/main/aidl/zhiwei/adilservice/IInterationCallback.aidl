// IInterationCallback.aidl
package zhiwei.adilservice;

//import java.util.Map;
// Declare any non-default types here with import statements

interface IInterationCallback {
    void onReceiveInterationCallback(String json);
    //void onReceiveInterationCallback(in Map<String, String> map);
}
