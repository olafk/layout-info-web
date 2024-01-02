package de.olafkock.liferay.layoutinfo.portlet;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferenceValueLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.DefaultStyleBookEntryUtil;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	private static final String JAVAX_PORTLET_TITLE = "javax.portlet.title.";

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
		renderRequest.setAttribute("styleBookEntryId", layout.getStyleBookEntryId());
		renderRequest.setAttribute("layoutPrototypeUuid", layout.getLayoutPrototypeUuid());
		renderRequest.setAttribute("masterLayoutPlid", layout.getMasterLayoutPlid());
		renderRequest.setAttribute("parentLayoutId", layout.getParentLayoutId());
		renderRequest.setAttribute("parentLayoutPlid", layout.getParentPlid());
		try {
			renderRequest.setAttribute("ancestorPlid", layout.getAncestorPlid());
		} catch (PortalException e1) {
			renderRequest.setAttribute("ancestorPlid", e1.getClass().getName() + " " + e1.getMessage());
		}
		try {
			renderRequest.setAttribute("ancestorLayoutId", layout.getAncestorLayoutId());
		} catch (PortalException e1) {
			renderRequest.setAttribute("ancestorLayoutId", e1.getClass().getName() + " " + e1.getMessage());
		}
		try {
			FrontendTokenDefinition frontendTokenDefinition = _frontendTokenDefinitionRegistry.getFrontendTokenDefinition(layout.getThemeId());
			Collection<FrontendToken> tokens = frontendTokenDefinition.getFrontendTokens();
		    String tokensString = tokens.stream()
		    	      .map(n -> n.getName())
		    	      .collect(Collectors.joining(", ", "{", "}"));
		    	 
			renderRequest.setAttribute("styleBookTokens", tokensString);
			StyleBookEntry styleBookEntry = DefaultStyleBookEntryUtil.getDefaultMasterStyleBookEntry(themeDisplay.getLayout());
			renderRequest.setAttribute("styleBookEntryName", styleBookEntry.getName());
			
		} catch (Exception e) {
			renderRequest.setAttribute("styleBookEntryName", e.getClass().getName() + " " + e.getMessage());
		}
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
			sb.append("<h3>").append(getPortletName(p.getPortletId(), themeDisplay.getLocale())).append("</h3>").append("<ul>");
			sb.append("<li>PortletId=").append(p.getPortletId()).append("</li>");
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
			sb.append("<li>ownerType=").append(getOwnerType(p.getOwnerType())).append("</li>");
			sb.append("<li>plid=").append(p.getPlid()).append("</li>");

			sb.append("</ul>");
		}
		
		renderRequest.setAttribute("portletPreferences", sb.toString());
		super.doView(renderRequest, renderResponse);
	}
	
	private String getPortletName(String portletId, Locale locale) {
		String result = portletId;
		int instanceIndex = portletId.indexOf("_INSTANCE_");
		if(instanceIndex>=0) {
			result = LanguageUtil.get(locale, JAVAX_PORTLET_TITLE + portletId.substring(0, instanceIndex));
		} else {
			result = LanguageUtil.get(locale, JAVAX_PORTLET_TITLE + portletId);
		}
		if(result.startsWith(JAVAX_PORTLET_TITLE)) {
			result = result.substring(JAVAX_PORTLET_TITLE.length());
		}
		return result;
	}

	private String getOwnerType(int numericOwnerType) {
		if(OWNER_TYPE_NAMES.containsKey(numericOwnerType)) {
			return "" + numericOwnerType + " " + OWNER_TYPE_NAMES.get(numericOwnerType);
		}
		return "" + numericOwnerType + " (unknown)";
	}
	
	static final HashMap<Integer, String> OWNER_TYPE_NAMES = new HashMap<Integer, String>();
	
	{ 
		OWNER_TYPE_NAMES.put( PortletKeys.PREFS_OWNER_TYPE_ARCHIVED, "(archived)");
		OWNER_TYPE_NAMES.put( PortletKeys.PREFS_OWNER_TYPE_COMPANY, "(company)");
		OWNER_TYPE_NAMES.put( PortletKeys.PREFS_OWNER_TYPE_GROUP, "(group)");
		OWNER_TYPE_NAMES.put( PortletKeys.PREFS_OWNER_TYPE_LAYOUT, "(layout)");
		OWNER_TYPE_NAMES.put( PortletKeys.PREFS_OWNER_TYPE_ORGANIZATION, "(organization)");
		OWNER_TYPE_NAMES.put( PortletKeys.PREFS_OWNER_TYPE_USER, "(user)");
	}

	
	@Reference(unbind="-") 
	LayoutLocalService layoutLocalService;
	
	@Reference(unbind="-")
	PortletPreferencesLocalService ppls;

	@Reference(unbind="-")
	PortletPreferenceValueLocalService ppvlc;
	
	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;
	
	@Reference
	PortletPreferencesFactory ppf;
	
}