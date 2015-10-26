package com.example.administrador.agenda.controller.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrador.agenda.R;
import com.example.administrador.agenda.controller.adapters.AgendaAdapter;
import com.example.administrador.agenda.model.entidade.Agenda;
import com.example.administrador.agenda.model.persistence.agenda.AgendaRepository;
import com.example.administrador.agenda.model.services.AgendaBusinessService;
import com.example.administrador.agenda.tabs.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wanilton on 01/10/2015.
 */
public class AgendaListActivity extends AppCompatActivity{

    public static final String PARAM_AGENDA = "AGENDA";
    private ListView listViewAgenda;
    private Agenda selectAgenda;
    private Toolbar toolbar;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda_list);

        bindToolbar();
        bindAgendaList();
        bindFragment();
        bindPager();
        bindTabs();

    }

    private void bindPager() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
    }

    private void bindTabs() {
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);
        slidingTabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));
        slidingTabLayout.setViewPager(viewPager);
    }

    private void bindFragment() {
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
    }


    private void bindToolbar() {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void bindAgendaList() {
        List<Agenda> values = new ArrayList<>();
        listViewAgenda = (ListView) findViewById(R.id.listViewAgendaList);
        registerForContextMenu(listViewAgenda);
        listViewAgenda.setAdapter(new AgendaAdapter(this, values));
        listViewAgenda.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AgendaAdapter adapter = (AgendaAdapter) listViewAgenda.getAdapter();
                selectAgenda = adapter.getItem(position);
                return false;

            }
        });
    }

    @Override
    protected void onResume() {
        updateAgendaList();
        super.onResume();
    }

    private void updateAgendaList() {
        List<Agenda> values = AgendaBusinessService.findAll();
        AgendaAdapter adapter = (AgendaAdapter) listViewAgenda.getAdapter();
        adapter.setItens(values);
        adapter.notifyDataSetChanged();
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_agenda_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                onMenuAddClick();
                break;
            case R.id.menu_filter:
                onMenuFilterClick();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onMenuFilterClick() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                List<Agenda> listaNomeFilter = AgendaRepository.getAgendaByNome(value);
                AgendaAdapter adapter = (AgendaAdapter) listViewAgenda.getAdapter();
                adapter.setItens(listaNomeFilter);
                adapter.notifyDataSetChanged();

            }
        })
        .setNeutralButton("Não", null)
                .create()
                .show();

    }

    private void onMenuAddClick() {
        Intent goToTaskFormActivity = new Intent(AgendaListActivity.this, AgendaFormActivity.class);
        startActivity(goToTaskFormActivity);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_agenda_context_list, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_excluir:
                onMenuDeleteClick();
                break;
            case R.id.menu_editar:
                onMenuEditClick();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void onMenuEditClick() {
        Intent goToTaskForm = new Intent(AgendaListActivity.this, AgendaFormActivity.class);
        goToTaskForm.putExtra(AgendaListActivity.PARAM_AGENDA, selectAgenda);
        startActivity(goToTaskForm);

    }

    private void onMenuDeleteClick() {
        new AlertDialog.Builder(AgendaListActivity.this)
                .setTitle("Confirmação")
                .setMessage("Realmente deseja deletar?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AgendaBusinessService.delete(selectAgenda);
                        selectAgenda = null;
                        String message = "Deletado com sucesso";
                        updateAgendaList();
                        Toast.makeText(AgendaListActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                })
                .setNeutralButton("Não", null)
                .create()
                .show();
    }


    class MyPageAdapter extends FragmentPagerAdapter{
        String[] tabText = getResources().getStringArray(R.array.tabs);
        int icons[] = {R.drawable.ic_action_shopping_cart, R.drawable.ic_communication_contacts, R.drawable.ic_device_settings_system_daydream};

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            MyFragment myFragment = MyFragment.getInstance(position);

            return myFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = getResources().getDrawable(icons[position]);
            drawable.setBounds(0, 0, 100, 100);
            ImageSpan imageSpan = new ImageSpan(drawable);
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, spannableString.length(), spannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannableString;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public static class MyFragment extends Fragment{
        TextView textView;

        public static MyFragment getInstance(int position){
            MyFragment myFragment = new MyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            myFragment.setArguments(bundle);
            return myFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View layout = inflater.inflate(R.layout.fragment_my, container, false);
            Bundle bundle = getArguments();
            textView = (TextView) layout.findViewById(R.id.textView_tabs_fragment_my);

            if (bundle != null) {
                textView.setText("The Number Of Tab is " + bundle.getInt("position"));
            }

            return layout;
        }
    }

}
