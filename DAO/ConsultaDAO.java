package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import gui.util.Alerts;
import guiControllers.EscolherConsultaAvaliarController;
import guiControllers.TelaLoginMedicoController;
import guiControllers.TelaLoginPacienteController;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class ConsultaDAO {

	// private ChoiceBox<String> choiceBoxConsultas;

	public static String cpfPacienteDaConsulta;

	public static int idConsultaEscolhida;

	public static Date dataEscolhida;

	public void initializeAgendarConsulta(ComboBox<String> comboBoxMedicosDisponiveis, DatePicker datePickerDatas) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		if (TelaLoginPacienteController.getPlanoLogado() != null) {
			String selectQuery = "SELECT * FROM medicoscadastrados WHERE plano_atendido = ? ";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

				preparedStatement.setString(1, TelaLoginPacienteController.getPlanoLogado());

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						String nome = resultSet.getString("nome");
						String especialidade = resultSet.getString("especialidade");
						String nomeEspecialidade = nome + " - " + especialidade;
						comboBoxMedicosDisponiveis.getItems().addAll(nomeEspecialidade);
					}
				} catch (SQLException e) {
					e.getMessage();
				}

			} catch (SQLException e) {
				e.getMessage();
			}
		} else {
			String selectQuery = "SELECT * FROM medicoscadastrados";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						String nome = resultSet.getString("nome");
						String especialidade = resultSet.getString("especialidade");
						String nomeEspecialidade = nome + " - " + especialidade;
						comboBoxMedicosDisponiveis.getItems().addAll(nomeEspecialidade);
					}
				} catch (SQLException e) {
					e.getMessage();
				}
			} catch (SQLException erro) {
				erro.printStackTrace();
			}
		}

		datePickerDatas.setDayCellFactory(new Callback<DatePicker, DateCell>() {
			@Override
			public DateCell call(DatePicker param) {
				return new DateCell() {
					@Override
					public void updateItem(LocalDate item, boolean empty) {
						super.updateItem(item, empty);
						if (item.isBefore(LocalDate.now())) {
							setDisable(true);
							setStyle("-fx-background-color: #D3D3D3;");
						}
					}
				};
			}
		});
	}

	public boolean medicoDisponivel(String nomeMedico, java.sql.Date dataEscolhida) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";
		String crm = null;

		String selectQuery = "SELECT * FROM medicoscadastrados WHERE nome = ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, nomeMedico);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					crm = resultSet.getString("crm");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		String countQuery = "SELECT COUNT(*) AS contador FROM consultas WHERE crm_Medico = ? AND dataConsulta = ? AND realizada = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(countQuery)) {

			preparedStatement.setString(1, crm);
			preparedStatement.setDate(2, dataEscolhida);
			preparedStatement.setString(3, "n");

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int numeroConsultas = resultSet.getInt("contador");
					if (numeroConsultas >= 4) {
						return false;
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean cliqueBotaoAgendamento(ComboBox<String> comboBoxMedicosDisponiveis, DatePicker datePickerDatas) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String medicoEscolhido = comboBoxMedicosDisponiveis.getValue();
		String[] partes = medicoEscolhido.split(" - ");
		String nomeMedico = partes[0];

		LocalDate dataDatePicker = datePickerDatas.getValue();
		java.sql.Date dataEscolhida = java.sql.Date.valueOf(dataDatePicker);

		String crmMedico = null;
		String cpfPaciente = TelaLoginPacienteController.getcpfLogado();

		String selectQuery = "SELECT * FROM medicoscadastrados WHERE nome = ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, nomeMedico);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					crmMedico = resultSet.getString("crm");
				}
			} catch (SQLException e) {
				e.getMessage();
				return false;
			}
		} catch (SQLException e) {
			e.getMessage();
			return false;
		}

		if (medicoDisponivel(nomeMedico, dataEscolhida) == false) {

			String insertQuery = "INSERT INTO consultasespera (crm_Medico, cpf_paciente, dataConsulta) VALUES (?, ?, ?)";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

				preparedStatement.setString(1, crmMedico);
				preparedStatement.setString(2, cpfPaciente);
				preparedStatement.setDate(3, dataEscolhida);

				int rowsAffected = preparedStatement.executeUpdate();

				if (rowsAffected > 0) {
					System.out.println("ADD A ESPERA");
					Alerts.showAlert("Agenda lotada!", null,
							"A agenda desse médico está lotada hoje! Você foi adicionado à lista de espera.",
							AlertType.INFORMATION);
				} else {
					System.out.println("NAO ADD A ESPERA");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("NAO ADD A ESPERA SQL EXCEPTION");
				return false;
			}
			comboBoxMedicosDisponiveis.setValue(null);
			datePickerDatas.setValue(null);
		}

		String insertQuery = "INSERT INTO consultas (crm_Medico, cpf_paciente, dataConsulta, realizada, valorConsulta) VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

			preparedStatement.setString(1, crmMedico);
			preparedStatement.setString(2, cpfPaciente);
			preparedStatement.setDate(3, dataEscolhida);
			preparedStatement.setString(4, "n");
			preparedStatement.setDouble(5, 0);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				comboBoxMedicosDisponiveis.setValue(null);
				datePickerDatas.setValue(null);
				return true;
			} else {
				comboBoxMedicosDisponiveis.setValue(null);
				datePickerDatas.setValue(null);
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			comboBoxMedicosDisponiveis.setValue(null);
			datePickerDatas.setValue(null);
		}
		return false;
	}

	public boolean avaliarConsulta(String textoAvaliacao, double estrelas) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		String updateQuery = "UPDATE consultas " + "SET textoAvaliacao = ?, " + "estrelas = ? " + "WHERE "
				+ "idConsulta = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

			preparedStatement.setString(1, textoAvaliacao);
			preparedStatement.setDouble(2, estrelas);
			preparedStatement.setInt(3, EscolherConsultaAvaliarController.idConsultaEscolhida);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("avaliacao c sucesso");
				return true;
			} else {
				System.out.println("avaliacao falhou");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void initializeCancelarConsulta(ComboBox<String> comboBoxConsultasMarcadas) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM consultas WHERE cpf_paciente = ? AND realizada = ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			System.out.println(TelaLoginPacienteController.getcpfLogado() + "cancelando cons");
			preparedStatement.setString(1, TelaLoginPacienteController.getcpfLogado());
			preparedStatement.setString(2, "n");

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int numeroConsulta = resultSet.getInt("id");
					String data = resultSet.getString("dataConsulta");
					String infoConsulta = "Nº da consulta: " + numeroConsulta + " - " + data;
					comboBoxConsultasMarcadas.getItems().addAll(infoConsulta);
				}
			} catch (SQLException e) {
				e.getMessage();
			}

		} catch (SQLException e) {
			e.getMessage();
		}
	}

	public String obterCrmMedico(int numeroConsulta) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String crm = null;

		String selectQuery = "SELECT crm_Medico FROM consultas WHERE id = ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setInt(1, numeroConsulta);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					crm = resultSet.getString("crm_Medico");
				}
			} catch (SQLException e) {
				e.getMessage();
			}
		} catch (SQLException e) {
			e.getMessage();
		}
		return crm;
	}

	public boolean cliqueBotaoCancelar(ComboBox<String> comboBoxConsultasMarcadas) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String consultaEscolhida = comboBoxConsultasMarcadas.getValue();
		System.out.println(consultaEscolhida + "consulta escolhida");
		String numeroDaConsulta = null;
		String[] partes = consultaEscolhida.split(" - ");
		String[] dadosConsulta = partes[0].split(": ");
		numeroDaConsulta = dadosConsulta[1];

		String cpf_paciente_espera = null;

		int numConsultaDesmarcada = Integer.parseInt(numeroDaConsulta);
		String crmMedico = obterCrmMedico(numConsultaDesmarcada);
		System.out.println(crmMedico + "crmMedico");
		System.out.println(numConsultaDesmarcada + "numConsultaDesmarcada");

		String selectQuery = "SELECT cpf_paciente FROM consultasespera WHERE crm_Medico = ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, crmMedico);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					cpf_paciente_espera = resultSet.getString("cpf_paciente");
					System.out.println("cpf nao nulo ");
				}
			} catch (SQLException e) {
				e.getMessage();
				return false;
			}
		} catch (SQLException e) {
			e.getMessage();
			return false;
		}

		String deleteQueryEspera = "DELETE FROM consultasespera WHERE crm_Medico = ? AND cpf_paciente = ? ORDER BY idEspera LIMIT 1";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(deleteQueryEspera)) {

			preparedStatement.setString(1, crmMedico);
			preparedStatement.setString(2, cpf_paciente_espera);

			int rowsAffected1 = preparedStatement.executeUpdate();

			if (rowsAffected1 > 0) {
				System.out.println("CONSULTAS ESPERA APAGADO C SUCESSO");
			} else {
				System.out.println("CONSULTAS ESPERA NAO APAGADO");
			}
		} catch (SQLException e) {
			e.getMessage();
		}

		if (cpf_paciente_espera == null) {
			String updateQuery = "DELETE FROM consultas WHERE id = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setInt(1, numConsultaDesmarcada);

				int rowsAffected2 = preparedStatement.executeUpdate();

				if (rowsAffected2 > 0) {
					Alerts.showAlert("Sucesso!", "Consulta desmarcada!", "", AlertType.CONFIRMATION);
					// labelMensagem.setText("Consulta desmarcada com sucesso!");
					System.out.println("UPDATE SIMMMMMMMMM RELAIAOD");
					return true;
				} else {
					Alerts.showAlert("Erro!", "Erro ao desmarcar consulta!", "Tente novamente!", AlertType.ERROR);
					// labelMensagem.setText("Não foi possível desmarcar a consulta. Tente
					// novamente");
					System.out.println("UPDATE NAAAAAAAO REALIADO");
					return false;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			String updateQuery = "UPDATE consultas SET cpf_paciente = ? WHERE id = ?";
			try (Connection connection = DriverManager.getConnection(url, username, password);
					PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

				preparedStatement.setString(1, cpf_paciente_espera);
				preparedStatement.setInt(2, numConsultaDesmarcada);

				int rowsAffected2 = preparedStatement.executeUpdate();

				if (rowsAffected2 > 0) {
					Alerts.showAlert("Sucesso!", "Consulta desmarcada!", "", AlertType.CONFIRMATION);
					// labelMensagem.setText("Consulta desmarcada com sucesso!");
					System.out.println("UPDATE SIMMMMMMMMM RELAIAOD");
					return true;
				} else {
					Alerts.showAlert("Erro!", "Erro ao desmarcar consulta!", "Tente novamente!", AlertType.ERROR);
					// labelMensagem.setText("Não foi possível desmarcar a consulta. Tente
					// novamente");
					System.out.println("UPDATE NAAAAAAAO REALIADO");
					return false;
				}

			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	public void initializeEscolherConsultaAvaliar(ChoiceBox<String> choiceBoxConsultas) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM consultas WHERE cpf_paciente = ? AND estrelas = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, TelaLoginPacienteController.getcpfLogado());
			preparedStatement.setDouble(2, 0);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					int idConsulta = resultSet.getInt("id");
					Date dataConsulta = resultSet.getDate("dataConsulta");
					String infoConsulta = "Nº consulta: " + idConsulta + " - " + "Data: " + dataConsulta;
					choiceBoxConsultas.getItems().addAll(infoConsulta);
				}
			} catch (SQLException e) {
				e.getMessage();
			}

		} catch (SQLException e) {
			e.getMessage();
		}
	}

	public void initializeEscolherConsultaRealizar(ChoiceBox<String> choiceBoxConsultas) {
		String url = "jdbc:mysql://localhost:3306/hospital?useSSL=false";
		String username = "root";
		String password = "86779791";

		String selectQuery = "SELECT * FROM consultas WHERE crm_Medico = ? AND realizada = ?";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, TelaLoginMedicoController.getcrmLogado());
			preparedStatement.setString(2, "n");

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String cpfPaciente = resultSet.getString("cpf_paciente");
					idConsultaEscolhida = resultSet.getInt("id");
					String data = resultSet.getString("dataConsulta");
					dataEscolhida = resultSet.getDate("dataConsulta");
					String infoConsulta = "CPF do paciente: " + cpfPaciente + " - " + data;
					choiceBoxConsultas.getItems().add(infoConsulta);
				}
			} catch (SQLException e) {
				e.getMessage();
			}
		} catch (SQLException e) {
			e.getMessage();
		}
	}

	public void initializeRealizarConsulta(TextField textFieldPreco) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		String planoDoPaciente = null;

		String selectQuery = "SELECT plano FROM pacientes WHERE cpf = ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, ConsultaDAO.cpfPacienteDaConsulta);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					planoDoPaciente = resultSet.getString("plano");
				}
			} catch (SQLException e) {
				e.getMessage();
			}

		} catch (SQLException e) {
			e.getMessage();
		}

		if (planoDoPaciente == null) {
			textFieldPreco.setDisable(false);
		} else {
			textFieldPreco.setDisable(true);
		}
	}

	public boolean cliqueBotaoFinalizarConsulta(TextField textFieldPreco, String sintomas, String tratamento,
			String medicamentos, String exames) {
		String url = "jdbc:mysql://localhost:3306/hospital";
		String username = "root";
		String password = "86779791";

		String planoPaciente = null;

		String selectQuery = "SELECT plano FROM pacientes WHERE cpf = ? ";
		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setString(1, ConsultaDAO.cpfPacienteDaConsulta);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					planoPaciente = resultSet.getString("plano");
				}
			} catch (SQLException e) {
				e.getMessage();
			}

		} catch (SQLException e) {
			e.getMessage();
		}

		double valorConsulta;

		if (planoPaciente == null) {
			String preco = textFieldPreco.getText();
			valorConsulta = Double.parseDouble(preco);
		} else {
			valorConsulta = 0;
		}

		String updateQuery = "UPDATE consultas SET crm_Medico = ?, cpf_paciente = ?, dataConsulta = ?, realizada = ?, valorConsulta = ?, textoAvaliacao = ?, estrelas = ?, sintomas = ?, tratamento = ?, exames = ?, medicamentos = ? "
				+ "WHERE id = ?";

		try (Connection connection = DriverManager.getConnection(url, username, password);
				PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

			preparedStatement.setString(1, TelaLoginMedicoController.getcrmLogado());
			preparedStatement.setString(2, ConsultaDAO.cpfPacienteDaConsulta);
			preparedStatement.setDate(3, ConsultaDAO.dataEscolhida);
			preparedStatement.setString(4, "s");
			preparedStatement.setDouble(5, valorConsulta);
			preparedStatement.setString(6, "");
			preparedStatement.setDouble(7, 0);
			preparedStatement.setString(8, sintomas);
			preparedStatement.setString(9, tratamento);
			preparedStatement.setString(10, exames);
			preparedStatement.setString(11, medicamentos);
			preparedStatement.setInt(12, ConsultaDAO.idConsultaEscolhida);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("cosnulta add as realizas");
				return true;
			} else {
				System.out.println("consultas naaaaaaaao add as realizasd");
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}