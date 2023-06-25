package com.axelor.service.impl;

import com.axelor.apps.base.db.Language;
import com.axelor.apps.base.db.repo.LanguageRepository;
import com.axelor.db.JPA;
import com.axelor.meta.db.MetaSelectItem;
import com.axelor.service.SelectionRestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.inject.Inject;
import wslite.json.JSONArray;
import wslite.json.JSONException;
import wslite.json.JSONObject;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectionRestUtilImpl implements SelectionRestUtil {

    private static final String SQL_META_SELECT_TABLE_NAME = "meta_select";
    private static final String SQL_META_SELECT_ITEM_TABLE_NAME = "meta_select_item";
    private static final String SQL_META_TRANSLATION_TABLE_NAME = "meta_translation";
    private static final String SQL_META_SELECT_ITEM = "select * from %s self where" +
            " self.select_id = (select sub_self.id from %s sub_self where sub_self.name" +
            " = :selectionName)";
    private static final String SQL_META_TRANSLATION = "(select message_value from %s mt " +
            "where mt.message_key = self.%s and mt.language = '%s') as %s_%s";

    //title is translatable, and should be first on position
    private final List<String> columns = List.of("title", "value", "order_seq");
    @Inject
    private LanguageRepository languageRepository;

    @Override
    public List<?> findByName(String name) {
        return this.findByName(name, false);
    }

    @Override
    public List<?> findByName(String name, Boolean translate) {
        return this.findByName(name, translate, columns);
    }

    @Override
    public List<?> findByName(String name, Boolean translate, List<String> columns) {
        List<String> columnNames = new ArrayList<>(columns);
        StringBuilder fields = new StringBuilder();
        columns.forEach(field -> fields.append("self.").append(field).append(","));

        if (translate && columns.size() > 0) {
            List<Language> languages = languageRepository.all().fetch();
            String translatableColumn = columns.get(0);
            for (Language language : languages) {
                String code = language.getCode();
                if (code == null || code.length() != 2) continue;
                String translateField = String.format(SQL_META_TRANSLATION, SQL_META_TRANSLATION_TABLE_NAME,
                        translatableColumn, code, translatableColumn, code);
                fields.append(translateField).append(",");
                columnNames.add(translatableColumn + "_" + code);
            }
        }

        String sql = String.format(SQL_META_SELECT_ITEM, SQL_META_SELECT_ITEM_TABLE_NAME, SQL_META_SELECT_TABLE_NAME);
        if (fields.length() > 0) {
            if (fields.toString().endsWith(",")) {
                fields.replace(fields.length() - 1, fields.length(), "");
            }
            sql = sql.replaceFirst("\\*", fields.toString());
        }
        Query query = JPA.em().createNativeQuery(sql);
        query.setParameter("selectionName", name);
        List<Object[]> resultList = query.getResultList();
        return toJsonArray(resultList, columnNames);
    }

    private JSONArray toJsonArray(List<Object[]> objectList, List<String> fieldNames) {
        try {
            JSONArray array = new JSONArray();
            for (int i = 0; i < objectList.size(); i++) {
                Object[] objects = objectList.get(i);
                JSONObject jsonObject = new JSONObject();
                for (int j = 0; j < objects.length; j++) {
                    jsonObject.put(fieldNames.get(j), objects[j]);
                }
                array.add(jsonObject);
            }
            return array;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
