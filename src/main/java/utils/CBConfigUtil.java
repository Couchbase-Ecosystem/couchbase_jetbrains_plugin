package utils;

import java.util.List;

public class CBConfigUtil {

    public static boolean isSupported(String specifiedVersion) {
        String shouldBeHigherThen = "6.6";
        String[] specifiedSegments = specifiedVersion.split("\\.");
        String[] comparisonSegments = shouldBeHigherThen.split("\\.");

        for (int i = 0; i < Math.max(specifiedSegments.length, comparisonSegments.length); i++) {
            int specifiedSegment = i < specifiedSegments.length ? Integer.parseInt(specifiedSegments[i]) : 0;
            int comparisonSegment = i < comparisonSegments.length ? Integer.parseInt(comparisonSegments[i]) : 0;

            if (specifiedSegment > comparisonSegment) {
                return true;
            } else if (specifiedSegment < comparisonSegment) {
                return false;
            }
        }
        return false;
    }

    public static boolean hasQueryService(List<String> services) {
        if(services == null) {
            return false;
        }
        return services.contains("n1ql");
    }
}
