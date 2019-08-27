package iaf.ofek.hadracha.base_course.web_server.Data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InMemoryMapDataBase implements CrudDataBase {

    private Map<Integer, Entity> data = new HashMap<>();
    private int id=0;

    @Override
    public <T extends Entity> @Nullable T getByID(int id) throws ClassCastException {
        return (T) data.get(id).clone();
    }

    @Override
    public @NotNull <T extends Entity> List<T> getAllOfType(Class<T> type) {
        return data.values().stream().filter(o -> o.getClass()==type).map(o -> (T)o).collect(Collectors.toList());
    }

    @Override
    public <T extends Entity> int create(T entity) {
        int newId=id++;
        entity.setId(newId);
        data.put(newId,entity);
        return newId;
    }

    @Override
    public <T extends Entity> void update(T entity) throws IllegalArgumentException {
        if (!data.containsKey(entity.getId()))
            throw new IllegalArgumentException("No entity with this ID exists. Desired ID: " + entity.getId());
        data.put(entity.getId(), entity.clone());
    }

    @Override
    public void delete(int id) throws IllegalArgumentException {
        if (!data.containsKey(id))
            throw new IllegalArgumentException("No entity with this ID exists. Desired ID: " + id);
        data.remove(id);
    }
}
