package br.com.agenda.bean;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import br.com.agenda.dao.EventoDAO;
import br.com.agenda.modelo.Evento;

@ManagedBean(name = "eventoBean")
@ViewScoped
public class EventoBean implements Serializable {

	private static final long serialVersionUID = -2040229175003158138L;

	private ScheduleModel eventModel;

	private Evento evento;
	private List<Evento> listaEvento;
	private EventoDAO eDAO;

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public Evento getEvento() {
		return evento;
	}

	public void setEvento(Evento evento) {
		this.evento = evento;
	}

	public List<Evento> getListaEvento() {
		return listaEvento;
	}

	public void setListaEvento(List<Evento> listaEvento) {
		this.listaEvento = listaEvento;
	}

	public EventoDAO geteDAO() {
		return eDAO;
	}

	public void seteDAO(EventoDAO eDAO) {
		this.eDAO = eDAO;
	}

	@PostConstruct
	public void inicializar() {
		eDAO = new EventoDAO();
		evento = new Evento();

		eventModel = new DefaultScheduleModel();

		try {
			listaEvento = eDAO.todosEventos();
		} catch (SQLException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro no sql."));
		}

		for (Evento ev : listaEvento) {
			DefaultScheduleEvent evt = new DefaultScheduleEvent();

			evt.setEndDate(ev.getFim());
			evt.setStartDate(ev.getInicio());
			evt.setTitle(ev.getTitulo());
			evt.setData(ev.getId());
			evt.setDescription(ev.getDescricao());
			evt.setAllDay(true);
			evt.setEditable(true);
			
			if(ev.isStatus() == true){
				evt.setStyleClass("emp1");
			} else if (ev.isStatus() == false) {
				evt.setStyleClass("emp2");
			}
			
			eventModel.addEvent(evt);
		}

	}

	public void quandoSelecionado(SelectEvent selectEvent) {

		ScheduleEvent event = (ScheduleEvent) selectEvent.getObject();

		for (Evento ev : listaEvento) {
			if (ev.getId() == (Long) event.getData()) {
				evento = ev;
				break;
			}
		}
	}

	public void quandoNovo(SelectEvent selectEvent) {
		ScheduleEvent event = new DefaultScheduleEvent("", (Date) selectEvent.getObject(),
				(Date) selectEvent.getObject());
		evento = new Evento();
		evento.setInicio(new java.sql.Date(event.getStartDate().getTime()));
		evento.setFim(new java.sql.Date(event.getEndDate().getTime()));
	}

	public void salvar() {
		if (evento.getId() == null) {
			if (evento.getInicio().getTime() <= evento.getFim().getTime()) {
				eDAO = new EventoDAO();
				try {
					eDAO.salvar(evento);
					inicializar();
				} catch (SQLException e) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Erro ao salvar evento", "Erro: " + e.getMessage()));

				}
				evento = new Evento();

			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Data inicial não pode ser maior que a final", "Data inicial não pode ser maior que a final"));
			}
		} else {
			try {
				eDAO.atualizar(evento);
				inicializar();
			} catch (SQLException e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Erro ao salvar evento", "Erro: " + e.getMessage()));
			}
		}

	}

	public void quandoMovido(ScheduleEntryMoveEvent event) {
		for (Evento ev : listaEvento) {
			if (ev.getId() == (Long) event.getScheduleEvent().getData()) {
				evento = ev;
				eDAO = new EventoDAO();
				try {
					eDAO.atualizar(evento);
					inicializar();
				} catch (SQLException e) {
					e.printStackTrace();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Erro ao salvar evento", "Erro: " + e.getMessage()));
				}
				break;
			}
		}
	}

	public void quandoRedimensionado(ScheduleEntryResizeEvent event) {
		for (Evento ev : listaEvento) {
			if (ev.getId() == (Long) event.getScheduleEvent().getData()) {
				evento = ev;
				eDAO = new EventoDAO();
				try {
					eDAO.atualizar(evento);
					inicializar();
				} catch (SQLException e) {
					e.printStackTrace();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Erro ao salvar evento", "Erro: " + e.getMessage()));
				}
				break;
			}
		}
	}

}
