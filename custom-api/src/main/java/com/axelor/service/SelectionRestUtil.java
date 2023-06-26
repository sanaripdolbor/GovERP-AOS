package com.axelor.service;

import com.axelor.meta.db.MetaSelect;

import java.util.List;

public interface SelectionRestUtil {

    List<?> findByName(String name);

    List<?> findByName(String name, Boolean translate);
    List<?> findByName(String name, Boolean translate, List<String> columns);
}
