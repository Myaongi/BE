package Myaong.Gangajikimi.config;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JtsConfig {

    /**
     * JTS GeometryFactory를 Spring Bean으로 등록합니다.
     * SRID 4326 (WGS84) 좌표계를 사용하도록 설정합니다.
     */
    @Bean
    public GeometryFactory geometryFactory() {
        // SRID 4326은 GPS 위성 시스템에서 사용하는 표준 위경도 좌표계입니다.
        return new GeometryFactory(new PrecisionModel(), 4326);
    }
}