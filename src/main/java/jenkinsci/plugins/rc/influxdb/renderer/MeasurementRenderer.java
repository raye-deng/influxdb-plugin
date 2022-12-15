package jenkinsci.plugins.rc.influxdb.renderer;

public interface MeasurementRenderer<T> {

    String render(T input);
}
