package com.axelor.web;

import com.axelor.rpc.Request;
import com.axelor.rpc.Response;
import com.axelor.service.SelectionRestUtil;
import com.google.common.base.Strings;
import com.google.inject.Inject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/selection")
public class SelectionUtilRest {
    @Inject
    private SelectionRestUtil selectionRestUtil;

    @POST
    @Path("/{name}")
    public Response findByName(@PathParam("name") String name, Request request) {
        final Response response = new Response();
        if (request == null || Strings.isNullOrEmpty(name)) {
            return response.fail("invalid request");
        }
        if (request.getFields() != null && request.getFields().size() > 0) {
            response.setData(selectionRestUtil.findByName(name, request.isTranslate(), request.getFields()));
        } else {
            response.setData(selectionRestUtil.findByName(name, request.isTranslate()));
        }
        return response;
    }
}
