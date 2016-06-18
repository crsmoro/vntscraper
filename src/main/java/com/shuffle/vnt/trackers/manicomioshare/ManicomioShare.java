package com.shuffle.vnt.trackers.manicomioshare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.TorrentParser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;

public class ManicomioShare implements Tracker {

	private final String name = "ManicomioShare";

	private final String url = "https://www.manicomio-share.com/pesquisa.php?order=desc&sort=id";

	private final String authenticationUrl = "https://www.manicomio-share.com/";

	private final ParameterType parameterType = ParameterType.DEFAULT;

	private final String usernameField = "username";

	private final String passwordField = "password";

	private final String authenticationMethod = "POST";

	private final String pageField = "page";

	private final String searchField = "busca";

	private final String categoryField = "cat";

	private final List<TrackerCategory> categories = new ArrayList<>();

	{

		categories.add(new TrackerCategory("Filmes : 4K", "", "189"));
		categories.add(new TrackerCategory("Filmes : HD", "", "127"));
		categories.add(new TrackerCategory("Filmes : HD Nacionais", "", "148"));
		categories.add(new TrackerCategory("Filmes : HD 3D", "", "147"));
		categories.add(new TrackerCategory("Filmes : Nacionais", "", "128"));
		categories.add(new TrackerCategory("Filmes : Blu-Ray", "", "132"));
		categories.add(new TrackerCategory("Filmes : Blu-Ray Nacioinais", "", "152"));
		categories.add(new TrackerCategory("Filmes : Blu-Ray 3D", "", "141"));
		categories.add(new TrackerCategory("Filmes : Blu-Ray BD25", "", "142"));
		categories.add(new TrackerCategory("Filmes : Blu-Ray BD25 Nacionais", "", "182"));
		categories.add(new TrackerCategory("Filmes : Blu-Ray BD25 3D", "", "183"));
		categories.add(new TrackerCategory("Filmes : Blu-Ray Remux", "", "143"));
		categories.add(new TrackerCategory("Filmes : DVD-R", "", "34"));
		categories.add(new TrackerCategory("Filmes : DVD-R Nacionais", "", "134"));
		categories.add(new TrackerCategory("Filmes : DVD-R 9", "", "144"));
		categories.add(new TrackerCategory("Filmes : Documentarios DVD-R", "", "145"));
		categories.add(new TrackerCategory("Filmes : Documentarios HD", "", "151"));

		categories.add(new TrackerCategory("Filmes : Ação", "", "27"));
		categories.add(new TrackerCategory("Filmes : Animação", "", "95"));
		categories.add(new TrackerCategory("Filmes : Aventura", "", "28"));
		categories.add(new TrackerCategory("Filmes : Biografia", "", "29"));
		categories.add(new TrackerCategory("Filmes : Classicos", "", "20"));
		categories.add(new TrackerCategory("Filmes : Comédia", "", "31"));
		categories.add(new TrackerCategory("Filmes : Documentários", "", "32"));
		categories.add(new TrackerCategory("Filmes : Drama", "", "33"));
		categories.add(new TrackerCategory("Filmes : Esportes", "", "35"));
		categories.add(new TrackerCategory("Filmes : Fantasia", "", "185"));
		categories.add(new TrackerCategory("Filmes : Ficção", "", "36"));
		categories.add(new TrackerCategory("Filmes : Guerra", "", "85"));
		categories.add(new TrackerCategory("Filmes : Infantil", "", "37"));
		categories.add(new TrackerCategory("Filmes : Musicais", "", "118"));
		categories.add(new TrackerCategory("Filmes : Policial", "", "104"));
		categories.add(new TrackerCategory("Filmes : Suspense", "", "40"));
		categories.add(new TrackerCategory("Filmes : Religiosos", "", "38"));
		categories.add(new TrackerCategory("Filmes : Romance", "", "39"));
		categories.add(new TrackerCategory("Filmes : Terror", "", "41"));
		categories.add(new TrackerCategory("Filmes : Western", "", "107"));

		categories.add(new TrackerCategory("Fotos: Fotos", "", "42"));

		categories.add(new TrackerCategory("Jogos: Dreamcast", "", "97"));
		categories.add(new TrackerCategory("Jogos: Emuladores e Rom", "", "44"));
		categories.add(new TrackerCategory("Jogos: Game Cube", "", "101"));
		categories.add(new TrackerCategory("Jogos: Mac OS", "", "140"));
		categories.add(new TrackerCategory("Jogos: Nintendo DS", "", "119"));
		categories.add(new TrackerCategory("Jogos: PC", "", "45"));
		categories.add(new TrackerCategory("Jogos: PS1", "", "87"));
		categories.add(new TrackerCategory("Jogos: PS2", "", "46"));
		categories.add(new TrackerCategory("Jogos: PS3", "", "120"));
		categories.add(new TrackerCategory("Jogos: PSP", "", "82"));
		categories.add(new TrackerCategory("Jogos: XBOX", "", "47"));
		categories.add(new TrackerCategory("Jogos: XBOX 360", "", "48"));
		categories.add(new TrackerCategory("Jogos: Wii", "", "100"));
		categories.add(new TrackerCategory("Jogos: Wii-U", "", "187"));

		categories.add(new TrackerCategory("Livros: E-books", "", "49"));

		categories.add(new TrackerCategory("Musicas: Axé", "", "50"));
		categories.add(new TrackerCategory("Musicas: Blues", "", "51"));
		categories.add(new TrackerCategory("Musicas: Coletânea", "", "53"));
		categories.add(new TrackerCategory("Musicas: Country", "", "103"));
		categories.add(new TrackerCategory("Musicas: Discografia", "", "102"));
		categories.add(new TrackerCategory("Musicas: Dance", "", "54"));
		categories.add(new TrackerCategory("Musicas: Eletronica", "", "55"));
		categories.add(new TrackerCategory("Musicas: Enka e Japonesa", "", "135"));
		categories.add(new TrackerCategory("Musicas: Erudita", "", "52"));
		categories.add(new TrackerCategory("Musicas: Forró", "", "56"));
		categories.add(new TrackerCategory("Musicas: Funk", "", "57"));
		categories.add(new TrackerCategory("Musicas: Gospel", "", "58"));
		categories.add(new TrackerCategory("Musicas: Hard Core", "", "117"));
		categories.add(new TrackerCategory("Musicas: Hard Rock", "", "59"));
		categories.add(new TrackerCategory("Musicas: Heavy Metal", "", "60"));
		categories.add(new TrackerCategory("Musicas: Hip Hop", "", "61"));
		categories.add(new TrackerCategory("Musicas: House", "", "90"));
		categories.add(new TrackerCategory("Musicas: Infantil", "", "62"));
		categories.add(new TrackerCategory("Musicas: Instrumental", "", "175"));
		categories.add(new TrackerCategory("Musicas: Jazz", "", "86"));
		categories.add(new TrackerCategory("Musicas: MPB", "", "63"));
		categories.add(new TrackerCategory("Musicas: New Age", "", "64"));
		categories.add(new TrackerCategory("Musicas: Oldies", "", "94"));
		categories.add(new TrackerCategory("Musicas: Pagode", "", "65"));
		categories.add(new TrackerCategory("Musicas: Pop", "", "66"));
		categories.add(new TrackerCategory("Musicas: Psychedelic", "", "109"));
		categories.add(new TrackerCategory("Musicas: Punk Rock", "", "67"));
		categories.add(new TrackerCategory("Musicas: Raízes", "", "89"));
		categories.add(new TrackerCategory("Musicas: Rap", "", "68"));
		categories.add(new TrackerCategory("Musicas: Reggae", "", "69"));
		categories.add(new TrackerCategory("Musicas: Regionais", "", "70"));
		categories.add(new TrackerCategory("Musicas: Pop", "", "71"));
		categories.add(new TrackerCategory("Musicas: Religiosas", "", "66"));
		categories.add(new TrackerCategory("Musicas: Rock", "", "72"));
		categories.add(new TrackerCategory("Musicas: Samba", "", "73"));
		categories.add(new TrackerCategory("Musicas: Sertanejo", "", "74"));
		categories.add(new TrackerCategory("Musicas: Soul R&amp;B", "", "98"));
		categories.add(new TrackerCategory("Musicas: Surf Music", "", "110"));
		categories.add(new TrackerCategory("Musicas: Techno", "", "92"));
		categories.add(new TrackerCategory("Musicas: Trance", "", "91"));
		categories.add(new TrackerCategory("Musicas: Trilha Sonora", "", "75"));
		categories.add(new TrackerCategory("Musicas: Vocal", "", "93"));
		categories.add(new TrackerCategory("Musicas: World Music", "", "111"));

		categories.add(new TrackerCategory("Novelas: Novela", "", "170"));
		categories.add(new TrackerCategory("Novelas: Novela DVD-R", "", "171"));
		categories.add(new TrackerCategory("Novelas: Novela HD", "", "172"));

		categories.add(new TrackerCategory("Religião: Religião Diversos", "", "179"));
		categories.add(new TrackerCategory("Religião: Religião DVD-R", "", "178"));

		categories.add(new TrackerCategory("Revistas: Revistas", "", "96"));
		categories.add(new TrackerCategory("Revistas: HQ", "", "99"));

		categories.add(new TrackerCategory("Séries: Blu-Ray", "", "186"));
		categories.add(new TrackerCategory("Séries: Seriados", "", "76"));
		categories.add(new TrackerCategory("Séries: HD", "", "122"));
		categories.add(new TrackerCategory("Séries: DVD-R", "", "124"));
		categories.add(new TrackerCategory("Séries: DVD-R 9", "", "181"));
		categories.add(new TrackerCategory("Séries: DVDRip", "", "125"));
		categories.add(new TrackerCategory("Séries: Cartoon", "", "123"));
		categories.add(new TrackerCategory("Séries: Cartoon DVD-R", "", "164"));
		categories.add(new TrackerCategory("Séries: Cartoon HD", "", "163"));

		categories.add(new TrackerCategory("Shows: Shows", "", "77"));
		categories.add(new TrackerCategory("Shows: Shows Blu-Ray", "", "133"));
		categories.add(new TrackerCategory("Shows: Shows DVD-R", "", "130"));
		categories.add(new TrackerCategory("Shows: Shows DVD-R 9", "", "180"));
		categories.add(new TrackerCategory("Shows: Shows HD", "", "129"));

		categories.add(new TrackerCategory("Televisão: Televisão", "", "78"));
		categories.add(new TrackerCategory("Televisão: Televisão HDTV", "", "136"));
		categories.add(new TrackerCategory("Televisão: Televisão HD", "", "137"));
		categories.add(new TrackerCategory("Televisão: Televisão SDTV", "", "139"));

		categories.add(new TrackerCategory("Video: Video Clipes", "", "79"));
		categories.add(new TrackerCategory("Video: Video Clipes DVD-R", "", "167"));
		categories.add(new TrackerCategory("Video: Video Clipes HD", "", "168"));

		categories.add(new TrackerCategory("Anime: Anime", "", "21"));
		categories.add(new TrackerCategory("Anime: Anime DVD-R", "", "155"));
		categories.add(new TrackerCategory("Anime: Anime HD", "", "156"));

		categories.add(new TrackerCategory("Aplicativos: Linux", "", "22"));
		categories.add(new TrackerCategory("Aplicativos: Mac OS", "", "23"));
		categories.add(new TrackerCategory("Aplicativos: Windows", "", "24"));

		categories.add(new TrackerCategory("Apostila: Apostila e Textos", "", "25"));
		categories.add(new TrackerCategory("Apostila: Cursos e Video Aula", "", "169"));

		categories.add(new TrackerCategory("Celular: Aplicativos", "", "26"));
		categories.add(new TrackerCategory("Celular: Jogos", "", "184"));

		categories.add(new TrackerCategory("Desenhos: Desenhos", "", "88"));
		categories.add(new TrackerCategory("Desenhos: Desenhos DVD-R", "", "165"));
		categories.add(new TrackerCategory("Desenhos: Desenhos HD", "", "166"));

		categories.add(new TrackerCategory("Diversos: Diversos", "", "83"));

		categories.add(new TrackerCategory("Educativos: Infantil", "", "84"));

		categories.add(new TrackerCategory("Eventos: Esportivos", "", "105"));
		categories.add(new TrackerCategory("Eventos: Esportivos DVD-R", "", "153"));
		categories.add(new TrackerCategory("Eventos: Esportivos HD", "", "154"));

		categories.add(new TrackerCategory("Adulto: [XXX] - Filmes", "", "80"));
		categories.add(new TrackerCategory("Adulto: [XXX] - Blu-Ray", "", "157"));
		categories.add(new TrackerCategory("Adulto: [XXX] - DVD-R", "", "159"));
		categories.add(new TrackerCategory("Adulto: [XXX] - DVD-R 9", "", "160"));
		categories.add(new TrackerCategory("Adulto: [XXX] - Eróticos", "", "161"));
		categories.add(new TrackerCategory("Adulto: [XXX] - HD", "", "162"));
		categories.add(new TrackerCategory("Adulto: [XXX] - Fotos", "", "112"));
		categories.add(new TrackerCategory("Adulto: [XXX] - Hentai", "", "113"));
		categories.add(new TrackerCategory("Adulto: [XXX] - Jogos", "", "131"));

	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getAuthenticationUrl() {
		return authenticationUrl;
	}

	@Override
	public ParameterType getParameterType() {
		return parameterType;
	}

	@Override
	public boolean isAuthenticated(Body body) {
		return Jsoup.parse(body.getContent()).select("a[href=\"http://www.manicomio-share.com/account-recover.php\"]").size() <= 0;
	}

	@Override
	public String getUsernameField() {
		return usernameField;
	}

	@Override
	public String getPasswordField() {
		return passwordField;
	}

	@Override
	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	@Override
	public Map<String, String> getAuthenticationAdditionalParameters() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dados", "ok");
		return map;
	}

	@Override
	public String getPageField() {
		return pageField;
	}

	@Override
	public String getSearchField() {
		return searchField;
	}

	@Override
	public String getCategoryField() {
		return categoryField;
	}

	@Override
	public List<TrackerCategory> getCategories() {
		return categories;
	}

	@Override
	public String getPageValue(long page) {
		return String.valueOf(page);
	}

	@Override
	public TorrentParser getTorrentParser() {
		return new ManicomioShareTorrent();
	}

	@Override
	public TorrentDetailedParser getTorrentDetailedParser() {
		return new ManicomioShareTorrentDetail();
	}

}
