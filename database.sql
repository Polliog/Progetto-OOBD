--
-- PostgreSQL database dump
--

-- Dumped from database version 15.2
-- Dumped by pg_dump version 15.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: short_text; Type: DOMAIN; Schema: public; Owner: postgres
--

CREATE DOMAIN public.short_text AS character varying(128)
	CONSTRAINT short_text_check CHECK ((length((VALUE)::text) > 0));


ALTER DOMAIN public.short_text OWNER TO postgres;

--
-- Name: notification_request_update(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.notification_request_update() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
			DECLARE
			 	page_creator VARCHAR(255); 
				TYPE_REQUEST_UPDATE INT := 0;
			
			BEGIN
				SELECT author INTO page_creator
				FROM "Page"
				WHERE id = NEW.page_id;
		
				INSERT INTO "Notification" ("user", update_id, type)
				VALUES (page_creator, NEW.id, TYPE_REQUEST_UPDATE);
		
				RETURN NEW;
			END;
		$$;


ALTER FUNCTION public.notification_request_update() OWNER TO postgres;

--
-- Name: notification_update_accepted(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.notification_update_accepted() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
			DECLARE
				TYPE_UPDATE_ACCEPTED INT := 1;
				NOTIFICATION_READ INT := 1;
				STATUS_PENDING INT := -1;
				STATUS_ACCEPTED INT := 1;
				
				page_creator VARCHAR(255);
				
			BEGIN
				SELECT author INTO page_creator
				FROM "Page"
				WHERE id = NEW.page_id;
		
				IF OLD.status = STATUS_PENDING AND NEW.status = STATUS_ACCEPTED THEN
					INSERT INTO "Notification" ("user", update_id, type)
					VALUES (NEW.author, NEW.id, TYPE_UPDATE_ACCEPTED);
		
					UPDATE "Notification"
					SET type = TYPE_UPDATE_ACCEPTED, status = NOTIFICATION_READ
					WHERE "user" = page_creator AND update_id = NEW.id;
				END IF;
		
			RETURN NEW;
			END;
		$$;


ALTER FUNCTION public.notification_update_accepted() OWNER TO postgres;

--
-- Name: notification_update_rejected(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.notification_update_rejected() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
			DECLARE
				TYPE_UPDATE_REJECTED INT := 2;
				NOTIFICATION_READ INT := 1;
				STATUS_PENDING INT := -1;
				STATUS_REJECTED INT := 0;
				page_creator VARCHAR(255);
				
			BEGIN
				SELECT author INTO page_creator
				FROM "Page"
				WHERE id = NEW.page_id;
		
				IF OLD.status = STATUS_PENDING AND NEW.status = STATUS_REJECTED THEN
					INSERT INTO notification ("user", update_id, type)
					VALUES (NEW.author, NEW.id, TYPE_UPDATE_REJECTED);
		
					UPDATE "Notification"
					SET type = TYPE_UPDATE_REJECTED, status = NOTIFICATION_READ
					WHERE "user" = page_creator AND update_id = NEW.id;
				END IF;
		
			RETURN NEW;
			END;
		$$;


ALTER FUNCTION public.notification_update_rejected() OWNER TO postgres;

--
-- Name: search_notifications(text, integer, integer, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.search_notifications(titolo_pagina_text text, tipo_notifica integer, stato_notifica integer, notifica_letta boolean) RETURNS TABLE(id integer, user_name public.short_text, status integer, update_id integer, notification_type integer, viewed boolean, creation_date timestamp without time zone)
    LANGUAGE plpgsql
    AS $$
    	BEGIN
    		RETURN QUERY
    			SELECT 
    				n.id,
    				n."user",
    				n.status,
    				n.update_id,
    				n."type",
    				n.viewed,
    				n.creation_date
    			FROM "Notification" n
    			JOIN "PageUpdate" u ON n.update_id = u.id
    			JOIN "Page" p ON u.page_id = p.id
    				WHERE p.title ILIKE '%' || titolo_pagina_text || '%'
    				AND n.type = tipo_notifica
    				AND n.status = stato_notifica
    				AND n.viewed = notifica_letta;
    	END;
    	$$;


ALTER FUNCTION public.search_notifications(titolo_pagina_text text, tipo_notifica integer, stato_notifica integer, notifica_letta boolean) OWNER TO postgres;

--
-- Name: search_pages(public.short_text, boolean, boolean, boolean); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.search_pages(search_text public.short_text, search_in_title boolean DEFAULT true, search_in_text boolean DEFAULT false, search_in_author boolean DEFAULT false) RETURNS TABLE(page_id integer, title public.short_text, creation_date timestamp without time zone, author public.short_text)
    LANGUAGE plpgsql
    AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p."title", p.creation_date, p.author
    FROM "Page" p
    WHERE
    (search_in_title AND p."title" ILIKE '%' || search_text || '%') 
    OR
    (search_in_text AND p.id IN (
        SELECT pt.page_id 
        FROM "PageText" pt
        WHERE pt.text ILIKE '%' || search_text || '%')) 
    OR
    (search_in_author AND p.author ILIKE '%' || search_text || '%');
END;
$$;


ALTER FUNCTION public.search_pages(search_text public.short_text, search_in_title boolean, search_in_text boolean, search_in_author boolean) OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: Notification; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Notification" (
    id integer NOT NULL,
    "user" public.short_text NOT NULL,
    status integer DEFAULT 0,
    update_id integer NOT NULL,
    type integer NOT NULL,
    viewed boolean DEFAULT false,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public."Notification" OWNER TO postgres;

--
-- Name: Notification_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Notification_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Notification_id_seq" OWNER TO postgres;

--
-- Name: Notification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Notification_id_seq" OWNED BY public."Notification".id;


--
-- Name: Page; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."Page" (
    id integer NOT NULL,
    title public.short_text NOT NULL,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    author public.short_text NOT NULL
);


ALTER TABLE public."Page" OWNER TO postgres;

--
-- Name: PageText; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."PageText" (
    id integer NOT NULL,
    order_num integer NOT NULL,
    text text NOT NULL,
    page_id integer NOT NULL,
    author public.short_text NOT NULL
);


ALTER TABLE public."PageText" OWNER TO postgres;

--
-- Name: PageText_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."PageText_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."PageText_id_seq" OWNER TO postgres;

--
-- Name: PageText_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."PageText_id_seq" OWNED BY public."PageText".id;


--
-- Name: PageUpdate; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."PageUpdate" (
    id integer NOT NULL,
    page_id integer,
    author public.short_text DEFAULT NULL::character varying,
    status integer DEFAULT '-1'::integer,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    old_text text
);


ALTER TABLE public."PageUpdate" OWNER TO postgres;

--
-- Name: PageUpdate_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."PageUpdate_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."PageUpdate_id_seq" OWNER TO postgres;

--
-- Name: PageUpdate_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."PageUpdate_id_seq" OWNED BY public."PageUpdate".id;


--
-- Name: Page_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."Page_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."Page_id_seq" OWNER TO postgres;

--
-- Name: Page_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."Page_id_seq" OWNED BY public."Page".id;


--
-- Name: PagineConPiuModificheAccettate; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public."PagineConPiuModificheAccettate" AS
 SELECT subquery.title,
    max(subquery.num_accepted) AS num_accepted
   FROM ( SELECT "Page".id,
            "Page".title,
            count("PageUpdate".id) AS num_accepted
           FROM (public."Page"
             JOIN public."PageUpdate" ON (("Page".id = "PageUpdate".page_id)))
          WHERE ("PageUpdate".status = 1)
          GROUP BY "Page".id, "Page".title) subquery
  GROUP BY subquery.title;


ALTER TABLE public."PagineConPiuModificheAccettate" OWNER TO postgres;

--
-- Name: UpdatedText; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."UpdatedText" (
    id integer NOT NULL,
    text text,
    order_num integer,
    update_id integer,
    type integer NOT NULL
);


ALTER TABLE public."UpdatedText" OWNER TO postgres;

--
-- Name: UpdatedText_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public."UpdatedText_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public."UpdatedText_id_seq" OWNER TO postgres;

--
-- Name: UpdatedText_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public."UpdatedText_id_seq" OWNED BY public."UpdatedText".id;


--
-- Name: User; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public."User" (
    username public.short_text NOT NULL,
    password public.short_text NOT NULL,
    admin boolean DEFAULT false,
    creation_date timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public."User" OWNER TO postgres;

--
-- Name: modifichediunutenteadunapagina; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.modifichediunutenteadunapagina AS
 SELECT "User".username,
    "Page".title,
    "PageText".text AS new_text
   FROM ((public."User"
     JOIN public."Page" ON ((("User".username)::text = ("Page".author)::text)))
     JOIN public."PageText" ON (("Page".id = "PageText".page_id)));


ALTER TABLE public.modifichediunutenteadunapagina OWNER TO postgres;

--
-- Name: notifichediunutente; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.notifichediunutente AS
 SELECT "User".username,
    "Notification".status,
    "Notification".update_id,
    "Notification".type
   FROM (public."Notification"
     JOIN public."User" ON ((("Notification"."user")::text = ("User".username)::text)));


ALTER TABLE public.notifichediunutente OWNER TO postgres;

--
-- Name: paginacontesto; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.paginacontesto AS
 SELECT "Page".title,
    "Page".id AS page_id,
    "PageText".id AS text_id,
    "Page".author
   FROM (public."Page"
     LEFT JOIN public."PageText" ON (("Page".id = "PageText".page_id)));


ALTER TABLE public.paginacontesto OWNER TO postgres;

--
-- Name: pagineconpiumodificheaccettate; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.pagineconpiumodificheaccettate AS
 SELECT subquery.title,
    max(subquery.num_accepted) AS num_accepted
   FROM ( SELECT "Page".id,
            "Page".title,
            count("PageUpdate".id) AS num_accepted
           FROM ((public."Page"
             LEFT JOIN public."PageUpdate" ON (("Page".id = "PageUpdate".page_id)))
             LEFT JOIN public."Notification" ON (("PageUpdate".id = "Notification".update_id)))
          WHERE ("Notification".type = 1)
          GROUP BY "Page".id, "Page".title) subquery
  GROUP BY subquery.title;


ALTER TABLE public.pagineconpiumodificheaccettate OWNER TO postgres;

--
-- Name: paginediunutente; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.paginediunutente AS
 SELECT "User".username,
    "Page".title
   FROM (public."User"
     JOIN public."Page" ON ((("User".username)::text = ("Page".author)::text)));


ALTER TABLE public.paginediunutente OWNER TO postgres;

--
-- Name: updateperpagina; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.updateperpagina AS
 SELECT "Page".id AS page_id,
    count("PageUpdate".id) AS num_updates
   FROM (public."Page"
     LEFT JOIN public."PageUpdate" ON (("Page".id = "PageUpdate".page_id)))
  WHERE ("PageUpdate".status IS NOT NULL)
  GROUP BY "Page".id;


ALTER TABLE public.updateperpagina OWNER TO postgres;

--
-- Name: utenticonmodificheproposte; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.utenticonmodificheproposte AS
 SELECT "User".username,
    "PageUpdate".page_id
   FROM (public."PageUpdate"
     JOIN public."User" ON ((("PageUpdate".author)::text = ("User".username)::text)));


ALTER TABLE public.utenticonmodificheproposte OWNER TO postgres;

--
-- Name: Notification id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Notification" ALTER COLUMN id SET DEFAULT nextval('public."Notification_id_seq"'::regclass);


--
-- Name: Page id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Page" ALTER COLUMN id SET DEFAULT nextval('public."Page_id_seq"'::regclass);


--
-- Name: PageText id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageText" ALTER COLUMN id SET DEFAULT nextval('public."PageText_id_seq"'::regclass);


--
-- Name: PageUpdate id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageUpdate" ALTER COLUMN id SET DEFAULT nextval('public."PageUpdate_id_seq"'::regclass);


--
-- Name: UpdatedText id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UpdatedText" ALTER COLUMN id SET DEFAULT nextval('public."UpdatedText_id_seq"'::regclass);


--
-- Data for Name: Notification; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Notification" (id, "user", status, update_id, type, viewed, creation_date) FROM stdin;
4	prova	0	4	1	f	2024-02-03 18:35:31.851435
8	gio	0	6	1	f	2024-02-03 18:38:58.200106
7	mario	1	6	1	t	2024-02-03 18:38:45.441046
5	mario	1	5	1	t	2024-02-03 18:37:06.117465
3	mario	1	4	1	t	2024-02-03 17:48:49.379113
9	prova	0	2	1	f	2024-02-03 19:04:53.163462
1	mario	1	2	1	t	2024-02-03 17:27:30.652889
10	prova	0	3	1	f	2024-02-03 19:04:53.172203
2	mario	1	3	1	t	2024-02-03 17:48:21.033805
\.


--
-- Data for Name: Page; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."Page" (id, title, creation_date, author) FROM stdin;
1	La mia pagina	2024-02-03 17:21:18.598767	mario
2	12345	2024-02-03 17:48:39.928304	mario
4	Tesla (azienda)	2024-02-03 19:16:05.541842	prova
5	Pannello fotovoltaico	2024-02-03 19:18:30.794516	prova
6	Elon Musk	2024-02-03 19:19:50.050334	prova
7	Nikola Tesla	2024-02-03 19:20:43.485179	prova
8	Aeroponica	2024-02-03 19:29:28.207162	giuseppe
9	Agricoltura Smart	2024-02-03 19:32:07.298944	giuseppe
10	Metodi di Agricoltura Alternativa	2024-02-03 19:32:20.318751	giuseppe
\.


--
-- Data for Name: PageText; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."PageText" (id, order_num, text, page_id, author) FROM stdin;
9	0	12345	2	gio
10	1	1234	2	gio
11	2	dsd	2	gio
12	3	dsds	2	gio
17	0	<h1>Questa è la mia pagina</h1>	1	prova
18	1	Piacere mi chiamo <b>Mario</b>, questo colore è <font color='#800080'>Viola</font color>	1	prova
19	2	dsdsd	1	prova
130	0		8	giuseppe
131	1	L'aeroponica è una tecnologia di sviluppo in serra di piante senza l'utilizzo di terra o di qualsiasi altro aggregato di sostegno. 	8	giuseppe
132	2	Le piante, infatti, sono sostenute artificialmente e la loro alimentazione è garantita da sistemi di nebulizzazione di acqua, arricchita da fertilizzanti minerali, che investe direttamente l'apparato radicale della pianta.	8	giuseppe
133	3		8	giuseppe
134	4	<h1><b>L'aeroponica: Vantaggi e Svantaggi</b></h1>	8	giuseppe
135	5	L'aeroponica gode di <font color='#FF00FF'><i>vantaggi</i></font> e <font color='#FF00FF'><i>svantaggi</i></font> rispetto alle colture tradizionali. La necessità di vaporizzare le soluzioni nutritive richiede spazio coperto, per certe colture quindi sono state ideate prima strutture a piramide, poi strutture verticali a colonna, con evidente concentrazione delle colture per metro quadro.	8	giuseppe
136	6	La coltivazione in ambiente confinato, (chiuso) non sterile, umido e caldo, in presenza di nutrienti, ed in assenza o in carenza di irraggiamento solare diretto a funzione sterilizzatrice, rischia l'insorgenza sporadica di infestazioni batteriche o micotiche.	8	giuseppe
137	7	 Con l'areoponica si ha inoltre un consistente risparmio di acqua e in commercio sono presenti appositi kit per creare "indoor" una coltivazione aeroponica.	8	giuseppe
138	8		8	giuseppe
139	9	Resta non facile l'utilizzo di questa tecnica per le colture tradizionalmente estensive (grano, mais, etc) o di colture di piante che richiedono un notevole sviluppo vegetativo. Inoltre un impianto aeroponico può avere costi elevati.	8	giuseppe
140	0	<h1><b>Agricoltura Smart: Innovazione nel Mondo Agricolo</b></h1>	9	giuseppe
141	1		9	giuseppe
142	2	L'agricoltura smart rappresenta una rivoluzione nell'approccio tradizionale all'agricoltura, integrando tecnologie avanzate per ottimizzare la produzione e la sostenibilità. Questo approccio impiega sensori, intelligenza artificiale e sistemi automatizzati per monitorare e gestire le colture in tempo reale.	9	giuseppe
143	3		9	giuseppe
144	4	<font color='#FF00FF'><i>Vantaggi dell'Agricoltura Smart:</i></font>	9	giuseppe
145	5	- Ottimizzazione dell'uso delle risorse, come acqua e fertilizzanti, attraverso sistemi di monitoraggio precisi.	9	giuseppe
146	6	- Miglioramento della precisione nell'applicazione di pesticidi e fertilizzanti, riducendo gli impatti ambientali.	9	giuseppe
147	7	- Maggiore efficienza nei processi decisionali grazie all'analisi dei dati raccolti dai sensori.	9	giuseppe
148	8	- Miglioramento della produttività e della qualità delle colture.	9	giuseppe
149	9		9	giuseppe
150	10	<font color='#FF00FF'><i>Sfide e Considerazioni:</i></font>	9	giuseppe
151	11	- Investimenti iniziali elevati per l'implementazione di tecnologie avanzate.	9	giuseppe
152	12	- Necessità di competenze tecniche per la gestione e la manutenzione dei sistemi smart.	9	giuseppe
51	0	In ingegneria energetica un <b>pannello fotovoltaico</b>  è un dispositivo optoelettronico, composto da moduli fotovoltaici, 	5	prova
52	1	a loro volta costituiti da celle fotovoltaiche, in grado di convertire l'energia solare in energia elettrica mediante effetto fotovoltaico, tipicamente impiegato come generatore.	5	prova
53	0	<h1><b>Elon Reeve Musk</b></h1>	6	prova
54	1	 - Pretoria, 28 giugno 1971 - è un imprenditore sudafricano con cittadinanza canadese naturalizzato statunitense.	6	prova
55	2		6	prova
56	3	Ricopre i ruoli di fondatore, amministratore delegato e direttore tecnico della compagnia aerospaziale <u>SpaceX</u>, fondatore di <u>The Boring Company</u>, cofondatore di <u>Neuralink</u> e <u>OpenAI</u>, amministratore delegato e product architect della multinazionale automobilistica <u>Tesla</u>, proprietario e presidente di <u>X</u>. Ha inoltre proposto un sistema di trasporto superveloce conosciuto come <i>Hyperloop</i>, tuttora in fase di sviluppo.	6	prova
57	4		6	prova
153	13	- Possibili preoccupazioni etiche legate all'uso dei dati raccolti.	9	giuseppe
154	14		9	giuseppe
155	15	In conclusione, l'agricoltura smart promette di trasformare il settore agricolo, rendendolo più sostenibile ed efficiente grazie all'integrazione di tecnologie all'avanguardia.	9	giuseppe
58	5	Musk ha affermato che l'obiettivo di Tesla e SpaceX si concentra sull'ideale di migliorare il mondo e l'umanità. Uno dei suoi scopi è anche quello di ridurre il riscaldamento globale tramite l'utilizzo di energie rinnovabili e ridurre il rischio dell'estinzione umana o di catastrofi naturali stabilendo una colonia umana su <i>Marte</i>. Tramite <u>Starlink</u>, una costellazione di satelliti prodotta e gestita da SpaceX, vorrebbe invece fornire Internet ad alta velocità e bassa latenza a tutto il pianeta.	6	prova
59	6		6	prova
60	7	Secondo <i>Forbes</i>, al 20 dicembre 2023, con un patrimonio stimato di <font color='#FF00FF'>256,6 miliardi di dollari</font>, risulta essere la persona più ricca del mondo.	6	prova
61	0	<h1><b>Nikola Tesla</b></h1> (in serbo Никола Тесла?; Smiljan, 10 luglio 1856 – New York, 7 gennaio 1943) 	7	prova
62	1	è stato un inventore, fisico e ingegnere elettrico, nato da una famiglia serba nell'attuale territorio della Croazia durante il periodo dell'Impero austriaco, naturalizzato statunitense nel 1891.	7	prova
63	2		7	prova
64	3	Contribuì allo sviluppo di diversi settori delle scienze applicate, in particolare nel campo dell'elettromagnetismo, di cui fu un eminente pioniere, tra la fine dell'Ottocento e gli inizi del Novecento. I suoi brevetti e il suo lavoro teorico formano, in particolare, la base del sistema elettrico a corrente alternata, della distribuzione elettrica polifase e dei motori elettrici a corrente alternata, con i quali ha contribuito alla nascita della seconda rivoluzione industriale. A riconoscimento dei suoi contributi fu intitolata a suo nome, durante la Conférence générale des poids et mesures del 1960, l'unità di misura dell'induzione magnetica nel Sistema internazionale di unità di misura.	7	prova
95	0	<h1><b>Tesla, Inc.</b></h1> 	4	prova
96	1	(precedentemente Tesla Motors) è un'azienda statunitense con sede ad Austin in Texas, specializzata nella produzione di auto elettriche, <font color='#00BFFF'><a href='5'>pannelli fotovoltaici</a></font color>  e sistemi di stoccaggio energetico. È chiamata così in onore del noto inventore <font color='#0000FF'><a href='7'>Nikola Tesla</a></font color>.	4	prova
97	2		4	prova
98	3	L'obiettivo dell'azienda è quello di "<i>accelerare la transizione del mondo all'utilizzo di fonti di energia rinnovabili</i>". Questo include la produzione di veicoli elettrici ad alte prestazioni orientati al mercato di massa. Fondata nel 2003 a 	4	prova
99	4	San Carlos in California da <b>Martin Eberhard e Marc Tarpenning</b>, la società è cresciuta in organico fino a comprendere molti esperti mondiali di informatica e sistemi di calcolo, nel campo elettrico e dell'ingegneria elettrica ed elettronica.	4	prova
100	5		4	prova
101	6	Il CEO <a href='6'><b>Elon Musk</b></a> ha detto che immagina Tesla come una società tecnologica e una casa automobilistica indipendente, il cui fine è quello di offrire auto elettriche a prezzi accessibili al consumatore medio per promuovere l'utilizzo di fonti rinnovabili e non inquinanti.	4	prova
102	7		4	prova
103	8	Il <b>19 agosto 2015</b>, secondo una classifica di <i>Forbes</i>, Tesla era l'azienda più innovativa al mondo.	4	prova
104	9		4	prova
105	10	A <b>novembre 2018</b> i veicoli Tesla in circolazione hanno toccato il traguardo di <u>500,000 unità</u>, percorrendo <u>10 miliardi di miglia</u>.	4	prova
106	11		4	prova
107	12	Il <b>9 marzo 2020</b> ha raggiunto il traguardo di <u>1 milione di auto elettriche prodotte</u>, prima fra tutte le case automobilistiche.	4	prova
108	13		4	prova
109	14	Il <b>25 ottobre 2021</b> Tesla raggiunge una capitalizzazione azionaria di <font color='#FF00FF'><b>1,000 miliardi di dollari</b></font>, dopo un maxi ordine di <u>100,000 Tesla Model 3</u> da parte di <i>Hertz</i>, la prima volta per una casa automobilistica.	4	prova
194	0	<h1><b>Metodi di Agricoltura Alternativa: Diversificando l'Approccio Agricolo</b></h1>	10	giuseppe
195	1		10	giuseppe
196	2	L'adozione di metodi alternativi in agricoltura si propone di superare le limitazioni dei sistemi tradizionali, promuovendo pratiche più sostenibili e rispettose dell'ambiente. Questi approcci includono tecniche come l'aeroponica, l'idroponica e l'agricoltura verticale.	10	giuseppe
197	3		10	giuseppe
198	4	<a href='8'><i>Aeroponica:</i></a>	10	giuseppe
199	5	L'aeroponica coinvolge la coltivazione delle piante senza l'uso di suolo, utilizzando una soluzione nutritiva vaporizzata. Questo metodo permette un utilizzo efficiente delle risorse e una maggiore concentrazione di colture per metro quadro.	10	giuseppe
200	6		10	giuseppe
201	7	<font color='#ff9933'><i>Idroponica:</i></font>	10	giuseppe
202	8	Nell'idroponica, le piante crescono in soluzioni nutrienti liquide senza l'uso di terreno. Questo metodo offre un controllo preciso delle condizioni di crescita e riduce il consumo di acqua rispetto ai metodi tradizionali.	10	giuseppe
203	9		10	giuseppe
204	10	<font color='#ff9933'><i>Agricoltura Verticale:</i></font>	10	giuseppe
205	11	L'agricoltura verticale implica la coltivazione di piante in strati verticali, ottimizzando lo spazio. Questa tecnica è particolarmente adatta per l'ambiente urbano, riducendo la necessità di ampie estensioni di terreno.	10	giuseppe
206	12		10	giuseppe
207	13	<font color='#ff9933'><i>Vantaggi e Sfide:</i></font>	10	giuseppe
208	14	- Riduzione dell'impatto ambientale attraverso un uso più efficiente delle risorse.	10	giuseppe
209	15	- Maggiore resilienza alle condizioni climatiche avverse.	10	giuseppe
210	16	- Sfide legate ai costi iniziali e alla necessità di competenze specializzate.	10	giuseppe
211	17		10	giuseppe
212	18	In sintesi, l'adozione di metodi alternativi in agricoltura offre opp	10	giuseppe
\.


--
-- Data for Name: PageUpdate; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."PageUpdate" (id, page_id, author, status, creation_date, old_text) FROM stdin;
4	2	prova	1	2024-02-03 17:48:49.379113	12345\n
5	2	prova	1	2024-02-03 18:37:06.117465	12345\n1234\n
6	2	gio	1	2024-02-03 18:38:45.441046	12345\n1234\ndsd\n
2	1	prova	1	2024-02-03 17:27:30.652889	<h1>Questa è la mia pagina</h1>\nPiacere mi chiamo <b>Mario</b>, questo colore è <font color='#800080'>Viola</font color>\n
3	1	prova	1	2024-02-03 17:48:21.033805	<h1>Questa è la mia pagina</h1>\nPiacere mi chiamo <b>Mario</b>, questo colore è <font color='#800080'>Viola</font color>\n\nmodifica\n
\.


--
-- Data for Name: UpdatedText; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."UpdatedText" (id, text, order_num, update_id, type) FROM stdin;
1	<h1>Questa è la mia pagina</h1>	0	2	0
2	Piacere mi chiamo <b>Mario</b>, questo colore è <font color='#800080'>Viola</font color>	1	2	0
3		2	2	1
4	modifica	3	2	1
5	<h1>Questa è la mia pagina</h1>	0	3	0
6	Piacere mi chiamo <b>Mario</b>, questo colore è <font color='#800080'>Viola</font color>	1	3	0
7	dsdsd	2	3	1
8	12345	0	4	0
9	1234	1	4	1
10	12345	0	5	0
11	1234	1	5	0
12	dsd	2	5	1
13	12345	0	6	0
14	1234	1	6	0
15	dsd	2	6	0
16	dsds	3	6	1
\.


--
-- Data for Name: User; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public."User" (username, password, admin, creation_date) FROM stdin;
mario	mario	f	2024-02-03 17:13:58.224597
prova	prova	f	2024-02-03 17:24:01.588046
gio	gio	f	2024-02-03 18:38:40.02725
giuseppe	giuseppe	f	2024-02-03 19:25:37.558729
\.


--
-- Name: Notification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Notification_id_seq"', 10, true);


--
-- Name: PageText_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."PageText_id_seq"', 212, true);


--
-- Name: PageUpdate_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."PageUpdate_id_seq"', 6, true);


--
-- Name: Page_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."Page_id_seq"', 10, true);


--
-- Name: UpdatedText_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public."UpdatedText_id_seq"', 16, true);


--
-- Name: Notification notification_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Notification"
    ADD CONSTRAINT notification_pk PRIMARY KEY (id);


--
-- Name: Page page_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Page"
    ADD CONSTRAINT page_pk PRIMARY KEY (id);


--
-- Name: PageText pagetext_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageText"
    ADD CONSTRAINT pagetext_pk PRIMARY KEY (id);


--
-- Name: PageUpdate update_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageUpdate"
    ADD CONSTRAINT update_pk PRIMARY KEY (id);


--
-- Name: UpdatedText updatedtext_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UpdatedText"
    ADD CONSTRAINT updatedtext_pk PRIMARY KEY (id);


--
-- Name: User user_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."User"
    ADD CONSTRAINT user_pk PRIMARY KEY (username);


--
-- Name: PageUpdate trigger_update_accettazione_notifica; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_update_accettazione_notifica AFTER UPDATE ON public."PageUpdate" FOR EACH ROW EXECUTE FUNCTION public.notification_update_accepted();


--
-- Name: PageUpdate trigger_update_request_notifica; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_update_request_notifica AFTER INSERT ON public."PageUpdate" FOR EACH ROW EXECUTE FUNCTION public.notification_request_update();


--
-- Name: PageUpdate trigger_update_rifiuto_notifica; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER trigger_update_rifiuto_notifica AFTER UPDATE ON public."PageUpdate" FOR EACH ROW EXECUTE FUNCTION public.notification_update_rejected();


--
-- Name: Notification notification_fk_pageupdate; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Notification"
    ADD CONSTRAINT notification_fk_pageupdate FOREIGN KEY (update_id) REFERENCES public."PageUpdate"(id) ON DELETE CASCADE;


--
-- Name: Notification notification_fk_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Notification"
    ADD CONSTRAINT notification_fk_user FOREIGN KEY ("user") REFERENCES public."User"(username) ON DELETE CASCADE;


--
-- Name: Page page_fk_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."Page"
    ADD CONSTRAINT page_fk_user FOREIGN KEY (author) REFERENCES public."User"(username) ON DELETE CASCADE;


--
-- Name: PageText pagetext_fk_page; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageText"
    ADD CONSTRAINT pagetext_fk_page FOREIGN KEY (page_id) REFERENCES public."Page"(id) ON DELETE CASCADE;


--
-- Name: PageText pagetext_pk_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageText"
    ADD CONSTRAINT pagetext_pk_user FOREIGN KEY (author) REFERENCES public."User"(username);


--
-- Name: PageUpdate update_fk_page; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageUpdate"
    ADD CONSTRAINT update_fk_page FOREIGN KEY (page_id) REFERENCES public."Page"(id) ON DELETE CASCADE;


--
-- Name: PageUpdate update_fk_user; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."PageUpdate"
    ADD CONSTRAINT update_fk_user FOREIGN KEY (author) REFERENCES public."User"(username) ON DELETE CASCADE;


--
-- Name: UpdatedText updatedtext_fk_update; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public."UpdatedText"
    ADD CONSTRAINT updatedtext_fk_update FOREIGN KEY (update_id) REFERENCES public."PageUpdate"(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

