package org.hl7.fhir.r5.renderers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.hl7.fhir.exceptions.DefinitionException;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.exceptions.FHIRFormatError;
import org.hl7.fhir.r5.elementmodel.Element;
import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r5.model.Bundle.BundleEntryRequestComponent;
import org.hl7.fhir.r5.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.r5.model.Bundle.BundleEntrySearchComponent;
import org.hl7.fhir.r5.model.Bundle.BundleType;
import org.hl7.fhir.r5.model.Composition;
import org.hl7.fhir.r5.model.DomainResource;
import org.hl7.fhir.r5.model.Parameters;
import org.hl7.fhir.r5.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.r5.model.Provenance;
import org.hl7.fhir.r5.model.Resource;
import org.hl7.fhir.r5.renderers.utils.BaseWrappers.BaseWrapper;
import org.hl7.fhir.r5.renderers.utils.BaseWrappers.PropertyWrapper;
import org.hl7.fhir.r5.renderers.utils.BaseWrappers.ResourceWrapper;
import org.hl7.fhir.r5.renderers.utils.RenderingContext;
import org.hl7.fhir.r5.renderers.utils.Resolver.ResourceContext;
import org.hl7.fhir.r5.utils.EOperationOutcome;
import org.hl7.fhir.utilities.xhtml.NodeType;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;

public class ParametersRenderer extends ResourceRenderer {
  
  public ParametersRenderer(RenderingContext context, ResourceContext rcontext) {
    super(context, rcontext);
  }


  @Override
  public boolean render(XhtmlNode x, Resource r) throws FHIRFormatError, DefinitionException, IOException, FHIRException, EOperationOutcome {
    return false;
  }

  @Override
  public String display(Resource r) throws UnsupportedEncodingException, IOException {
    return null;
  }

  @Override
  public String display(ResourceWrapper r) throws UnsupportedEncodingException, IOException {
    return null;
  }

  @Override
  public boolean render(XhtmlNode x, ResourceWrapper params) throws FHIRFormatError, DefinitionException, IOException, FHIRException, EOperationOutcome {
    x.h2().tx("Parameters");
    XhtmlNode tbl = x.table("grid");
    PropertyWrapper pw = getProperty(params, "parameter");
    if (valued(pw)) {
      paramsW(tbl, pw.getValues(), 0);
    }
    return false;
  }
 
  private void paramsW(XhtmlNode tbl, List<BaseWrapper> list, int indent) throws FHIRFormatError, DefinitionException, FHIRException, IOException, EOperationOutcome {
    for (BaseWrapper p : list) {
      XhtmlNode tr = tbl.tr();
      XhtmlNode td = tr.td();
      for (int i = 0; i < indent; i++) {
        td.tx(XhtmlNode.NBSP);        
      }
      td.tx(p.get("name").primitiveValue());
      if (p.has("value")) {
        renderBase(tr.td(), p.get("value"));
      } else if (p.has("resource")) {
        ResourceWrapper rw = p.getChildByName("resource").getAsResource();
        td = tr.td();
        XhtmlNode para = td.para();
        para.tx(rw.fhirType()+"/"+rw.getId());
        para.an(rw.fhirType()+"_"+rw.getId()).tx(" ");
        XhtmlNode x = rw.getNarrative();
        if (x != null) {
          td.addChildren(x);
        } else {
          ResourceRenderer rr = RendererFactory.factory(rw, context, rcontext);
          rr.render(td, rw);
        }
      } else if (p.has("part")) {
        tr.td();
        PropertyWrapper pw = getProperty(p, "parameter");
        paramsW(tbl, pw.getValues(), 1);
      }
    }
  }
  
  public XhtmlNode render(Parameters params) throws FHIRFormatError, DefinitionException, IOException, FHIRException, EOperationOutcome {
    XhtmlNode div = new XhtmlNode(NodeType.Element, "div");
    div.h2().tx("Parameters");
    XhtmlNode tbl = div.table("grid");
    params(tbl, params.getParameter(), 0);
    return div;
  }

  private void params(XhtmlNode tbl, List<ParametersParameterComponent> list, int indent) throws FHIRFormatError, DefinitionException, FHIRException, IOException, EOperationOutcome {
    for (ParametersParameterComponent p : list) {
      XhtmlNode tr = tbl.tr();
      XhtmlNode td = tr.td();
      for (int i = 0; i < indent; i++) {
        td.tx(XhtmlNode.NBSP);        
      }
      td.tx(p.getName());
      if (p.hasValue()) {
        render(tr.td(), p.getValue());
      } else if (p.hasResource()) {
        ResourceRenderer rr = RendererFactory.factory(p.getResource(), context);
        rr.render(tr.td(), p.getResource());
      } else if (p.hasPart()) {
        tr.td();
        params(tbl, p.getPart(), 1);
      }
    }
  }

}
