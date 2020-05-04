package de.olafkock.liferay.layoutinfo.portlet;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.util.List;

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
		"javax.portlet.security-role-ref=power-user,user"
	},
	service = Portlet.class
)
public class LayoutInfoPortlet extends MVCPortlet {
	
	@Override
	public void doView(RenderRequest renderRequest, RenderResponse renderResponse)
			throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();
		renderRequest.setAttribute("info", layout.getTypeSettings());
		renderRequest.setAttribute("desc", layout.getDescription());
		
		List<PortletPreferences> portletPreferencesByPlid = 
				ppls.getPortletPreferencesByPlid(
						layout.getPlid());
		renderRequest.setAttribute("portletPreferences", portletPreferencesByPlid);

		super.doView(renderRequest, renderResponse);
	}
	
	@Reference(unbind="-") 
	LayoutLocalService layoutLocalService;
	
	@Reference(unbind="-")
	PortletPreferencesLocalService ppls;
	
}