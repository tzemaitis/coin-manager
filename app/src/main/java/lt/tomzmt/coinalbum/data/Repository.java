package lt.tomzmt.coinalbum.data;

import java.util.List;

import lt.tomzmt.coinalbum.data.entity.Coin;

/**
 * Created by t.zemaitis on 2014.12.08.
 */
public interface Repository<T> {

    T get(long id);

    long add(T entity);

    void remove(long id);

    void update(T entity);

    List<T> search(String key);
}
