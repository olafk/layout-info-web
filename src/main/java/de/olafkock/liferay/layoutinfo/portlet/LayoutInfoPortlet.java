package de.olafkock.liferay.layoutinfo.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import de.olafkock.liferay.layoutinfo.constants.LayoutInfoPortletKeys;

/**
 * @author olaf
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=true",
		"javax.portlet.display-name=LayoutInfo",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + LayoutInfoPortletKeys.LAYOUTINFO,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=power-user,user",
		"javax.portlet.preferences=<preference><name>portletSetupPortletDecoratorId</name><value>barebone</value></preference>"
	},
	service = Portlet.class
)
public class LayoutInfoPortlet extends MVCPortlet {
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		UnicodeProperties props = layout.getTypeSettingsProperties();
		StringBuffer sProps = new StringBuffer("<ul>");
		Set<String> keys = props.keySet();
		for (String key : keys) {
			sProps.append("<li>");
			sProps.append(key);
			sProps.append("\n <ul>\n");
			
			String value = props.get(key);
			String[] values = StringUtil.split(value);
			for (int i = 0; i < values.length; i++) {
				sProps.append("  <li>  ");
				sProps.append(values[i]);
				sProps.append("  </li>\n");
			}
			
			sProps.append("</ul>");
			sProps.append("</li>\n");
		}
		sProps.append("</ul>");
		
		renderRequest.setAttribute("layoutType", layout.getType());
		renderRequest.setAttribute("layoutName", layout.getName(themeDisplay.getLocale()));
		renderRequest.setAttribute("friendlyURL", layout.getFriendlyURL(themeDisplay.getLocale()));
		
		renderRequest.setAttribute("props", sProps.toString());
		if(layout.getDescription()==null || layout.getDescription().equals("")) {
			renderRequest.setAttribute("desc", "<i>empty</i>");
		} else {
			renderRequest.setAttribute("desc", layout.getDescription());
		}
		
		List<PortletPreferences> portletPreferencesByPlid = 
				ppls.getPortletPreferencesByPlid(
						layout.getPlid());
		StringBundler sb = new StringBundler();
		for (PortletPreferences p : portletPreferencesByPlid) {
			sb.append("<h3>").append(p.getPortletId()).append("</h3>").append("<ul>");
			sb.append("<li>PortletPreferences:<ul>");
			javax.portlet.PortletPreferences preferences = ppvlc.getPreferences(p);
			Map<String, String[]> map = preferences.getMap();
			Set<Entry<String, String[]>> entrySet = map.entrySet();
			for (Entry<String, String[]> entry : entrySet) {
				sb.append("<li>").append(entry.getKey()).append("=").append(entry.getValue()).append("</li>");
			}
			if(map.isEmpty()) {
				sb.append("<li><i>empty</i></li>");
			}
			
			sb.append("</ul></li>");

			sb.append("<li>companyId=").append(p.getCompanyId()).append("</li>");
			sb.append("<li>ctCollectionId=").append(p.getCtCollectionId()).append("</li>");
			sb.append("<li>mvccVersion=").append(p.getMvccVersion()).append("</li>");
			sb.append("<li>ownerId=").append(p.getOwnerId()).append("</li>");
			sb.append("<li>ownerType=").append(p.getOwnerType()).append("</li>");
			sb.append("<li>plid=").append(p.getPlid()).append("</li>");

			sb.append("</ul>");
		}
		
		renderRequest.setAttribute("portletPreferences", sb.toString());
		super.doView(renderRequest, renderResponse);
	}
	
	@Reference(unbind="-") 
	LayoutLocalService layoutLocalService;
	
	@Reference(unbind="-")
	PortletPreferencesLocalService ppls;

	@Reference(unbind="-")
	PortletPreferenceValueLocalService ppvlc;
	
	@Reference
	PortletPreferencesFactory ppf;
	
}