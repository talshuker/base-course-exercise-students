package iaf.ofek.hadracha.base_course.web_server.Utilities;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ListOperations {
    public interface Equalizer<T>{
        boolean isEqual(T o1, T o2);
    }

    public <T> List<T> subtract(List<T> list1, List<T> list2, Equalizer<T> equalizer){
        List<T> remaining = new ArrayList<>();
        list1.forEach(item -> {
            if (list2.stream().noneMatch(item2 -> equalizer.isEqual(item2, item)))
                remaining.add(item);
        });
        return remaining;
    }

    public <T> List<T> subtract(List<T> list1, List<T> list2){
        return subtract(list1, list2, Objects::equals);
    }
}
