package com.diascosta.cobranca.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.diascosta.cobranca.model.StatusTitulo;
import com.diascosta.cobranca.model.Titulo;
import com.diascosta.cobranca.repository.filter.TituloFilter;
import com.diascosta.cobranca.service.CadastroTituloService;

@Controller
@RequestMapping("/titulos")
public class TituloController {
	
	private static final String CADASTRO_TITULO = "CadastroTitulo";

	private static final String REDIRECT_TITULOS_NOVO = "redirect:/titulos/novo";
	
	@Autowired
	private CadastroTituloService cadastroTituloService;

	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView(CADASTRO_TITULO);
		mv.addObject(new Titulo());
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@Validated Titulo titulo, Errors errors, RedirectAttributes attributes) {						
		if(errors.hasErrors()) {
			return CADASTRO_TITULO;
		}		
		try {
			cadastroTituloService.save(titulo);						
			attributes.addFlashAttribute("mensagem", "Título salvo com sucesso!");		
			return REDIRECT_TITULOS_NOVO;
		} catch (IllegalArgumentException e) {
			errors.rejectValue("dataVencimento", null, e.getMessage());
			return CADASTRO_TITULO;
		}
	}
	
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") TituloFilter filtro) {
		ModelAndView mv = new ModelAndView("PesquisaTitulos");
		mv.addObject("titulos", cadastroTituloService.filtrar(filtro));
		return mv;
	}
	
	@RequestMapping("{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Titulo titulo) {
		ModelAndView mv = new ModelAndView(CADASTRO_TITULO);
		mv.addObject(titulo);
		return mv;
	}
	
	@RequestMapping(value = "{codigo}", method = RequestMethod.DELETE)
	public String excluir(@PathVariable Long codigo, RedirectAttributes attributes) {
		cadastroTituloService.delete(codigo);
		attributes.addFlashAttribute("mensagem", "Título excluído com sucesso!");
		return "redirect:/titulos";
	}
	
	@RequestMapping(value = "{codigo}/receber", method = RequestMethod.PUT)
	public @ResponseBody String receber(@PathVariable Long codigo) {
		return cadastroTituloService.receber(codigo);
	}
	
	@ModelAttribute
	public List<StatusTitulo> listaStatusTitulo() {
		return Arrays.asList(StatusTitulo.values());
	}
	
}
