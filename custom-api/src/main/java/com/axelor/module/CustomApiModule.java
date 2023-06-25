package com.axelor.module;

import com.axelor.app.AxelorModule;
import com.axelor.service.SelectionRestUtil;
import com.axelor.service.impl.SelectionRestUtilImpl;

public class CustomApiModule extends AxelorModule {

    @Override
    protected void configure() {
        bind(SelectionRestUtil.class).to(SelectionRestUtilImpl.class);
    }
}
