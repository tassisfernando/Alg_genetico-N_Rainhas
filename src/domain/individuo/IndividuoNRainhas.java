package domain.individuo;

import java.util.*;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Collections.shuffle;
import static utils.Utils.*;

public class IndividuoNRainhas extends Individuo {

    private final List<Integer> genes;
    private final int nRainhas;
    private final Random random;

    public IndividuoNRainhas(int nRainhas) {
        this.random = new Random();
        this.nRainhas = nRainhas;
        this.genes = geraGenes();
    }

    public IndividuoNRainhas(int nRainhas, List<Integer> genes) {
        this.random = new Random();
        this.nRainhas = nRainhas;
        this.genes = genes;
    }

    private List<Integer> geraGenes() {
        final int maxGenes = this.nRainhas;
        List<Integer> genes = iniciarLista(maxGenes);
        List<Integer> posicoes = criarPosicoes(maxGenes);

        for(int i = ZERO.intValue(); i < maxGenes; i++) {
            int pos = random.nextInt(posicoes.size());
            genes.add(pos, i);
            posicoes.remove(pos);
        }

        genes.removeIf(gene -> gene.equals(MENOS_UM));
        return genes;
    }

    /**
     * Retorna o fitness do indivíduo pelo número de colisões
     * @return fitness
     */
    @Override
    public Double avaliar() {
        Double nColisoes = ZERO.doubleValue();
        for(int i = ZERO.intValue(); i < this.nRainhas - ONE.intValue(); i++) {
            for (int j = ZERO.intValue(); j < this.nRainhas; j++) {
                int diag1 = this.genes.get(j) - Math.abs(j-i);
                int diag2 = this.genes.get(j) + Math.abs(j-i);
                if(this.genes.get(i).equals(diag1) || this.genes.get(i).equals(diag2)) {
                    nColisoes++;
                }
            }
        }

        return nColisoes;
    }

    /**
     * Retorna dois indivíduos após ser feita a permutação
     * @param p2: pai 2
     * @return uma lista com dois filhos
     */
    @Override
    public List<Individuo> getFilhos(Individuo p2) {
        if(p2 instanceof IndividuoNRainhas) {
            IndividuoNRainhas pai2 = (IndividuoNRainhas) p2;

            // posição do corte
            int posCorte = random.nextInt(this.nRainhas);

            List<Integer> genesInicioFilho1 = this.genes.subList(ZERO.intValue(), posCorte);
            List<Integer> genesFimFilho1 = pai2.getGenes().subList(posCorte, this.genes.size());

            List<Integer> genesInicioFilho2 = pai2.getGenes().subList(ZERO.intValue(), posCorte);
            List<Integer> genesFimFilho2 = this.genes.subList(posCorte, this.genes.size());

            List<Integer> genesFilho1 = new ArrayList<>(genesInicioFilho1);
            List<Integer> genesFilho2 = new ArrayList<>(genesInicioFilho2);

            List<Integer> genesRestoFilho1 = new ArrayList<>();
            List<Integer> genesRestoFilho2 = new ArrayList<>();

            // Verificando os repetidos
            encontraRepetidos(genesFimFilho2, genesFilho1, genesRestoFilho1);
            encontraRepetidos(genesFimFilho1, genesFilho2, genesRestoFilho2);

            // Embaralha os genes faltantes
            shuffle(genesRestoFilho1);
            shuffle(genesRestoFilho2);

            // Adiciona os genes faltantes aleatoriamente
            adicionaGenesFaltantes(genesFilho1, genesRestoFilho2);
            adicionaGenesFaltantes(genesFilho2, genesRestoFilho1);

            Individuo filho1 = new IndividuoNRainhas(this.nRainhas, genesFilho1);
            Individuo filho2 = new IndividuoNRainhas(this.nRainhas, genesFilho2);

            return Arrays.asList(filho1, filho2);
        }

        return null;
    }

    private void encontraRepetidos(List<Integer> genesFimOutroFilho, List<Integer> genesFilhoAtual, List<Integer> genesRestoFilhoAtual) {
        for(int i = ZERO.intValue(); i < genesFimOutroFilho.size(); i++) {
            int geneF2 = genesFimOutroFilho.get(i);
            if(genesFilhoAtual.contains(geneF2)) {
                genesFilhoAtual.add(MENOS_UM);
                genesRestoFilhoAtual.add(geneF2);
            } else {
                genesFilhoAtual.add(geneF2);
            }
        }
    }

    private void adicionaGenesFaltantes(List<Integer> genesFilhoAtual, List<Integer> genesResto) {
        while(genesFilhoAtual.contains(MENOS_UM) && !genesResto.isEmpty()) { //TODO alterei essa condição aqui
            int posGenInv = genesFilhoAtual.indexOf(MENOS_UM);
            int posResF2 = genesResto.size() - ONE.intValue();
            genesFilhoAtual.set(posGenInv, genesResto.get(posResF2));
            genesResto.remove(posResF2);
        }
    }

    /**
     * Retorna um indivíduo com duas posições aleatórias dos genes trocadas entre si
     * @return individuo mutante
     */
    @Override
    public Individuo getMutante() {
        List<Integer> posicoes = criarPosicoes(nRainhas);
        int pos1 =  random.nextInt(posicoes.size());
        posicoes.remove(pos1);
        int pos2 =  random.nextInt(posicoes.size());
        posicoes.remove(pos2);

        return new IndividuoNRainhas(nRainhas, trocaGenes(pos1, pos2));
    }

    private List<Integer> trocaGenes(int pos1, int pos2) {
        List<Integer> genesMut = new ArrayList<>(this.genes);
        Integer genPos1 = genesMut.get(pos1);
        genesMut.add(pos1, genesMut.get(pos2));
        genesMut.add(pos2, genPos1);
        return genesMut;
    }

    @Override
    public String toString() {
        return String.format("nRainhas: %s \nAvaliação: %s", nRainhas, avaliacao);
    }

    public List<Integer> getGenes() {
        return this.genes;
    }
}
